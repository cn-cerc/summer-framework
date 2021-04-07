package cn.cerc.db.mysql;

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
import cn.cerc.core.SqlText;
import cn.cerc.db.core.DataQuery;
import cn.cerc.db.core.ISessionOwner;

public class SqlQuery extends DataQuery {
    private static final Logger log = LoggerFactory.getLogger(SqlQuery.class);

    private static final long serialVersionUID = 7316772894058168187L;
    private SqlConnection connection;
    private SqlConnection slaveSession;

    private DataSource dataSource;
    private DataSource slaveDataSource;
    // 若数据有取完，则为true，否则为false
    private boolean fetchFinish;
    // 数据库保存操作执行对象
    private SqlOperator operator;
    // 仅当batchSave为true时，delList才有记录存在
    private List<Record> delList = new ArrayList<>();

    public SqlQuery(ISession session) {
        super(session);
        this.connection = (MysqlConnection) session.getProperty(MysqlConnection.sessionId);
        this.slaveSession = (SlaveMysqlConnection) session.getProperty(SlaveMysqlConnection.sessionId);

        this.dataSource = (DataSource) session.getProperty(MysqlConnection.dataSource);
        this.slaveDataSource = (DataSource) session.getProperty(SlaveMysqlConnection.slaveDataSource);
    }

    public SqlQuery(ISessionOwner owner) {
        this(owner.getSession());
    }

    @Override
    public void close() {
        this.active = false;
        this.operator = null;
        super.close();
    }

    private Statement getStatement(boolean isSlave) throws SQLException {
        try {
            if (isSlave) {
                if (this.slaveDataSource == null) {
                    if (this.dataSource == null) {
                        if (this.slaveSession == null) {
                            return this.connection.getClient().createStatement();
                        } else {
                            return this.slaveSession.getClient().createStatement();
                        }
                    } else {
                        return this.dataSource.getConnection().createStatement();
                    }
                } else {
                    return this.slaveDataSource.getConnection().createStatement();
                }
            } else {
                if (this.dataSource == null) {
                    return this.connection.getClient().createStatement();
                } else {
                    return this.dataSource.getConnection().createStatement();
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public DataQuery open() {
        if (connection == null) {
            throw new RuntimeException("SqlConnection is null");
        }
        return this._open(false);
    }

    public DataQuery openReadonly() {
        if (slaveSession == null) {
            throw new RuntimeException("SlaveConnection is null");
        }
        return this._open(true);
    }

    private DataQuery _open(boolean isSlave) {
        String sql = getSqlText().getCommand();
        Statement st = null;
        try {
            this.fetchFinish = true;
            st = this.getStatement(isSlave);
            log.debug(sql.replaceAll("\r\n", " "));
            st.execute(sql.replace("\\", "\\\\"));
            try (ResultSet rs = st.getResultSet()) {
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

    private void append(ResultSet rs) throws SQLException {
        DataSetEvent onAfterAppend = this.getOnAfterAppend();
        try {
            this.setOnAfterAppend(null);
            rs.last();
            if (getSqlText().getMaximum() > -1) {
                BigdataException.check(this, this.size() + rs.getRow());
            }
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
            if (rs.first()) {
                int total = this.size();
                do {
                    total++;
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
                } while (rs.next());
            }
        } finally {
            this.setOnAfterAppend(onAfterAppend);
        }
    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public SqlQuery setActive(boolean value) {
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

    public SqlOperator getDefaultOperator() {
        if (operator == null) {
            SqlOperator def = new SqlOperator(this.session);
            String sql = this.getSqlText().getText();
            if (sql != null) {
                def.setTableName(SqlOperator.findTableName(sql));
            }
            operator = def;
        }
        if (operator instanceof SqlOperator) {
            SqlOperator opear = operator;
            if (opear.getTableName() == null) {
                String sql = this.getSqlText().getText();
                if (sql != null) {
                    opear.setTableName(SqlOperator.findTableName(sql));
                }
            }
        }
        return operator;
    }

    @Override
    public IDataOperator getOperator() {
        return operator;
    }

    public void setOperator(SqlOperator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        StringBuffer sl = new StringBuffer();
        sl.append(String.format("[%s]%n", this.getClass().getName()));
        sl.append(String.format("CommandText:%s%n", this.getSqlText().getText()));
        sl.append(String.format("RecordCount:%d%n", this.size()));
        sl.append(String.format("RecNo:%d%n", this.getRecNo()));
        return sl.toString();
    }

    @Deprecated // 请改使用 getSqlText().getOffset
    public int getOffset() {
        return getSqlText().getOffset();
    }

    @Deprecated // 请改使用 getSqlText().setOffset
    public SqlQuery setOffset(int offset) {
        getSqlText().setOffset(offset);
        return this;
    }

    public int getMaximum() {
        return getSqlText().getMaximum();
    }

    public SqlQuery setMaximum(int maximum) {
        getSqlText().setMaximum(maximum);
        return this;
    }

    @Deprecated // 请改使用 getSqlText().getCommand
    public String getSelectCommand() {
        return getSqlText().getCommand();
    }

    public boolean getFetchFinish() {
        return fetchFinish;
    }

    public void clear() {
        close();
        this.getSqlText().clear();
    }

    @Override
    public SqlText getSqlText() {
        return super.getSqlText();
    }

    @Override
    public void setSqlText(SqlText sqlText) {
        super.setSqlText(sqlText);
    }

    @Override
    public SqlQuery add(String sql) {
        super.add(sql);
        return this;
    }

    @Override
    public SqlQuery add(String format, Object... args) {
        super.add(format, args);
        return this;
    }

}
