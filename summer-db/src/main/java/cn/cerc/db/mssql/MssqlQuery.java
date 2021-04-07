package cn.cerc.db.mssql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSetEvent;
import cn.cerc.core.DataSetState;
import cn.cerc.core.FieldDefs;
import cn.cerc.core.IDataOperator;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.core.DataQuery;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.BigdataException;

public class MssqlQuery extends DataQuery {
    private static final Logger log = LoggerFactory.getLogger(MssqlQuery.class);

    private static final long serialVersionUID = 889285738942368226L;

    private MssqlConnection connection;

    private DataSource dataSource;

    // 若数据有取完，则为true，否则为false
    private boolean fetchFinish;

    // 数据库保存操作执行对象
    private MssqlOperator operator;

    // 仅当batchSave为true时，delList才有记录存在
    private List<Record> delList = new ArrayList<>();

    public MssqlQuery(ISession session) {
        super(session);
        this.connection = (MssqlConnection) session.getProperty(MssqlConnection.sessionId);
        this.dataSource = (DataSource) session.getProperty(MssqlConnection.dataSource);
        this.getSqlText().setSupportMssql(true);
    }

    public MssqlQuery(IHandle session) {
        this(session.getSession());
    }

    @Override
    public void close() {
        this.active = false;
        this.operator = null;
        super.close();
    }

    @Override
    public DataQuery open() {
        if (connection == null) {
            throw new RuntimeException("MssqlConnection is null");
        }

        String sql = getSqlText().getCommand();
        Statement st = null;
        try {
            this.fetchFinish = true;
            st = this.getStatement();
            log.debug(sql.replaceAll("\r\n", " "));
            sql = sql.replace("\\", "\\\\");

            try (ResultSet rs = st.executeQuery(sql)) {
                // 取出所有数据
                append(rs);
                this.first();
                this.active = true;
                return this;
            }
        } catch (SQLException e) {
            log.error(sql);
            throw new RuntimeException(e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                st = null;
            }
        }
    }

    private void append(ResultSet rs) throws SQLException {
        DataSetEvent onAfterAppend = this.getOnAfterAppend();
        try {
            this.setOnAfterAppend(null);

            // 取得字段清单
            ResultSetMetaData meta = rs.getMetaData();
            FieldDefs defs = this.getFieldDefs();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String field = meta.getColumnLabel(i);
                if (!defs.exists(field)) {
                    defs.add(field);
                }
            }

            // 取得所有数据
            int total = this.size();
            while (rs.next()) {
                total++;
                if (getSqlText().getMaximum() > -1) {
                    BigdataException.check(this, total - this.size());
                }

                if (this.getMaximum() > -1 && this.getMaximum() < total) {
                    this.fetchFinish = false;
                    break;
                }
                Record record = this.newRecord();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String fn = rs.getMetaData().getColumnLabel(i);
                    record.setField(fn, rs.getObject(fn));
                }
                record.setState(DataSetState.dsNone);
                this.append(record);
            }
        } finally {
            this.setOnAfterAppend(onAfterAppend);
        }
    }

    private Statement getStatement() throws SQLException {
        try {
            if (this.dataSource == null) {
                return this.connection.getClient().createStatement();
            } else {
                return this.dataSource.getConnection().createStatement();
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public int getMaximum() {
        return getSqlText().getMaximum();
    }

    public MssqlQuery setMaximum(int maximum) {
        getSqlText().setMaximum(maximum);
        return this;
    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public MssqlQuery setActive(boolean value) {
        if (value) {
            if (!this.active) {
                this.open();
            }
            this.active = true;
        } else {
            this.close();
        }
        return this;
    }

    @Override
    public void post() {
        if (this.isBatchSave()) {
            return;
        }
        Record record = this.getCurrent();
        if (record.getState() == DataSetState.dsInsert) {
            beforePost();
            getDefaultOperator().insert(record);
            super.post();
        } else if (record.getState() == DataSetState.dsEdit) {
            beforePost();
            getDefaultOperator().update(record);
            super.post();
        }
    }

    @Override
    public void delete() {
        Record record = this.getCurrent();
        super.delete();
        if (record.getState() == DataSetState.dsInsert) {
            return;
        }
        if (this.isBatchSave()) {
            delList.add(record);
        } else {
            getDefaultOperator().delete(record);
        }
    }

    @Override
    public void save() {
        if (!this.isBatchSave()) {
            throw new RuntimeException("batchSave is false");
        }
        IDataOperator operator = getDefaultOperator();
        // 先执行删除
        for (Record record : delList) {
            operator.delete(record);
        }
        delList.clear();
        // 再执行增加、修改
        this.first();
        while (this.fetch()) {
            if (this.getState().equals(DataSetState.dsInsert)) {
                beforePost();
                operator.insert(this.getCurrent());
                super.post();
            } else if (this.getState().equals(DataSetState.dsEdit)) {
                beforePost();
                operator.update(this.getCurrent());
                super.post();
            }
        }
    }

    public MssqlOperator getDefaultOperator() {
        if (operator == null) {
            MssqlOperator def = new MssqlOperator(this.session);
            String sql = this.getSqlText().getText();
            if (sql != null) {
                def.setTableName(MssqlOperator.findTableName(sql));
                def.setUpdateKey("UpdateKey_");
            }
            operator = def;
        }
        if (operator instanceof MssqlOperator) {
            MssqlOperator opear = operator;
            if (opear.getTableName() == null) {
                String sql = this.getSqlText().getText();
                if (sql != null) {
                    opear.setTableName(MssqlOperator.findTableName(sql));
                }
            }
        }
        return operator;
    }

    // 追加相同数据表的其它记录，与已有记录合并
    public int attach(String sql) {
        if (!this.active) {
            this.clear();
            this.add(sql);
            this.open();
            return this.size();
        }
        if (connection == null) {
            throw new RuntimeException("SqlSession is null");
        }
        Connection conn = connection.getClient();
        if (conn == null) {
            throw new RuntimeException("Connection is null");
        }
        try {
            try (Statement st = conn.createStatement()) {
                log.debug(sql.replaceAll("\r\n", " "));
                st.execute(sql.replace("\\", "\\\\"));
                try (ResultSet rs = st.getResultSet()) {
                    int oldSize = this.size();
                    append(rs);
                    return this.size() - oldSize;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void clear() {
        close();
        this.getSqlText().clear();
    }

    @Override
    public IDataOperator getOperator() {
        return operator;
    }

    public void setOperator(MssqlOperator operator) {
        this.operator = operator;
    }

    public boolean getFetchFinish() {
        return fetchFinish;
    }

    @Override
    public MssqlQuery add(String sql) {
        super.add(sql);
        return this;
    }

    @Override
    public MssqlQuery add(String format, Object... args) {
        super.add(format, args);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        build.append(String.format("[%s]%n", this.getClass().getName()));
        build.append(String.format("CommandText:%s%n", this.getSqlText().getText()));
        build.append(String.format("RecordCount:%d%n", this.size()));
        build.append(String.format("RecNo:%d%n", this.getRecNo()));
        log.info("mssql {}", build.toString());
        return build.toString();
    }

}
