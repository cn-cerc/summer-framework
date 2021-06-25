package cn.cerc.db.mssql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.DataSetEvent;
import cn.cerc.core.DataSetState;
import cn.cerc.core.FieldDefs;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.SqlText;
import cn.cerc.db.core.BigdataException;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlOperator;

@SuppressWarnings("serial")
public class MssqlQuery extends DataSet implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(MssqlQuery.class);
    private MssqlServer client;
    private boolean active;
    // 若数据有取完，则为true，否则为false
    private boolean fetchFinish;
    // 数据库保存操作执行对象
    private SqlOperator operator;
    // 仅当batchSave为true时，delList才有记录存在
    private List<Record> delList = new ArrayList<>();
    private ISession session;
    // 批次保存模式，默认为post与delete立即保存
    private boolean batchSave = false;
    private SqlText sqlText = new SqlText();

    public MssqlQuery() {
        super();
    }

    public MssqlQuery(IHandle handle) {
        super();
        this.session = handle.getSession();
        this.client = (MssqlServer) getSession().getProperty(MssqlServer.SessionId);
        this.getSqlText().setServerType(SqlText.SERVERTYPE_MSSQL);
    }

    @Override
    public void close() {
        setActive(false);
        this.operator = null;
        super.close();
    }

    public MssqlQuery open() {
        if (client == null) {
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
                this.setActive(true);
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
            deleteStorage(record);
        }
    }

    @Override
    public void post() {
        if (this.isBatchSave()) {
            return;
        }
        Record record = this.getCurrent();
        if (record.getState() == DataSetState.dsInsert) {
            beforePost();
            insertStorage(record);
            super.post();
        } else if (record.getState() == DataSetState.dsEdit) {
            beforePost();
            updateStorage(record);
            super.post();
        }
    }

    private Statement getStatement() throws SQLException {
        return this.client.getClient().createStatement();
    }

    public int getMaximum() {
        return getSqlText().getMaximum();
    }

    public MssqlQuery setMaximum(int maximum) {
        getSqlText().setMaximum(maximum);
        return this;
    }

    public boolean isActive() {
        return active;
    }

    protected MssqlQuery setActive(boolean value) {
        this.active = value;
        return this;
    }

    public void save() {
        if (!this.isBatchSave()) {
            throw new RuntimeException("batchSave is false");
        }
        SqlOperator operator = getOperator();
        // 先执行删除
        for (Record record : delList) {
            operator.delete(client.getClient(), record);
        }
        delList.clear();
        // 再执行增加、修改
        this.first();
        while (this.fetch()) {
            if (this.getState().equals(DataSetState.dsInsert)) {
                beforePost();
                operator.insert(client.getClient(), this.getCurrent());
                super.post();
            } else if (this.getState().equals(DataSetState.dsEdit)) {
                beforePost();
                operator.update(client.getClient(), this.getCurrent());
                super.post();
            }
        }
    }

    public SqlOperator getOperator() {
        if (operator == null)
            operator = getDefaultOperator();
        if (operator.getTableName() == null) {
            String sql = this.getSqlText().getText();
            if (sql != null)
                operator.setTableName(SqlText.findTableName(sql));
        }
        return operator;
    }

    public void setOperator(MssqlOperator operator) {
        this.operator = operator;
    }

    // 追加相同数据表的其它记录，与已有记录合并
    public int attach(String sql) {
        if (!this.isActive()) {
            this.clear();
            this.add(sql);
            this.open();
            return this.size();
        }
        if (client == null) {
            throw new RuntimeException("SqlSession is null");
        }
        Connection conn = client.getClient();
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

    public boolean getFetchFinish() {
        return fetchFinish;
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

    public MssqlQuery add(String sql) {
        sqlText.add(sql);
        return this;
    }

    public MssqlQuery add(String format, Object... args) {
        sqlText.add(format, args);
        return this;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    public SqlText getSqlText() {
        return sqlText;
    }

    // 是否批量保存
    public boolean isBatchSave() {
        return batchSave;
    }

    public void setBatchSave(boolean batchSave) {
        this.batchSave = batchSave;
    }

    private void insertStorage(Record record) {
        getOperator().insert(client.getClient(), record);
    }

    private void updateStorage(Record record) {
        getOperator().update(client.getClient(), record);
    }

    private void deleteStorage(Record record) {
        getOperator().delete(client.getClient(), record);
    }

    private SqlOperator getDefaultOperator() {
        return new MssqlOperator(this);
    }
}
