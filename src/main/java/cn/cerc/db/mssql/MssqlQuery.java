package cn.cerc.db.mssql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.DataSetEvent;
import cn.cerc.core.FieldDefs;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.RecordState;
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
    private SqlOperator operator;
    private ISession session;
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

    public final MssqlQuery open() {
        this.setStorage(true);
        open(false);
        return this;
    }

    public final MssqlQuery openReadonly() {
        this.setStorage(false);
        open(true);
        return this;
    }

    public void open(boolean slaveServer) {
        if (client == null)
            throw new RuntimeException("MssqlConnection is null");

        this.fetchFinish = true;
        String sql = getSqlText().getCommand();
        try {
            try (Statement st = this.getStatement()) {
                log.debug(sql.replaceAll("\r\n", " "));
                sql = sql.replace("\\", "\\\\");

                try (ResultSet rs = st.executeQuery(sql)) {
                    // 取出所有数据
                    append(rs);
                    this.first();
                    this.setActive(true);
                }
            }
        } catch (SQLException e) {
            log.error(sql);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void append(ResultSet rs) throws SQLException {
        DataSetEvent afterAppend = this.getAfterAppend();
        try {
            this.onAfterAppend(null);

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
                record.setState(RecordState.dsNone);
                this.append(record);
            }
        } finally {
            this.onAfterAppend(afterAppend);
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

    public final void save() {
        if (!this.isBatchSave())
            throw new RuntimeException("batchSave is false");
        if (this.isStorage()) {
            SqlOperator operator = getOperator();
            // 先执行删除
            for (Record record : delList)
                operator.delete(client.getClient(), record);
            // 再执行增加、修改
            this.first();
            while (this.fetch()) {
                if (this.getCurrent().getState().equals(RecordState.dsInsert)) {
                    beforePost();
                    operator.insert(client.getClient(), this.getCurrent());
                    afterPost();
                } else if (this.getCurrent().getState().equals(RecordState.dsEdit)) {
                    beforePost();
                    operator.update(client.getClient(), this.getCurrent());
                    afterPost();
                }
            }
        }
        delList.clear();
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

    // 是否批量保存
    @Override
    public final boolean isBatchSave() {
        return super.isBatchSave();
    }

    @Override
    public final void setBatchSave(boolean batchSave) {
        super.setBatchSave(batchSave);
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

    @Override
    protected final void insertStorage(Record record) {
        getOperator().insert(client.getClient(), record);
    }

    @Override
    protected final void updateStorage(Record record) {
        getOperator().update(client.getClient(), record);
    }

    @Override
    protected final void deleteStorage(Record record) {
        getOperator().delete(client.getClient(), record);
    }

    private SqlOperator getDefaultOperator() {
        return new MssqlOperator(this);
    }

}
