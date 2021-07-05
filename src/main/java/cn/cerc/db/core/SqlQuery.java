package cn.cerc.db.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.FieldDefs;
import cn.cerc.core.FieldMeta.FieldType;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.RecordState;
import cn.cerc.core.SqlText;

@SuppressWarnings("serial")
public abstract class SqlQuery extends DataSet implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(SqlQuery.class);
    // 数据集是否有打开
    private boolean active = false;
    // 若数据有取完，则为true，否则为false
    private boolean fetchFinish;
    // 仅当batchSave为true时，delList才有记录存在
    private List<Record> delList = new ArrayList<>();
    // 数据库保存操作执行对象
    private SqlOperator operator;
    // SqlCommand 指令
    private SqlText sqlText = new SqlText();
    // 运行环境
    private ISession session;

    public SqlQuery() {
        super();
    }

    public SqlQuery(IHandle handle) {
        super();
        this.session = handle.getSession();
    }

    @Override
    public final ISession getSession() {
        return session;
    }

    @Override
    public final void setSession(ISession session) {
        this.session = session;
    }

    @Override
    public final void close() {
        this.setActive(false);
        this.operator = null;
        super.close();
    }

    public final SqlQuery open() {
        open(true);
        return this;
    }

    public final SqlQuery openReadonly() {
        open(false);
        return this;
    }

    private final void open(boolean masterServer) {
        this.setStorage(masterServer);
        this.setFetchFinish(true);
        String sql = getSqlText().getCommand();
        log.debug(sql.replaceAll("\r\n", " "));
        try (ConnectionClient client = getConnectionClient()) {
            try (Statement st = client.getConnection().createStatement()) {
                try (ResultSet rs = st.executeQuery(sql.replace("\\", "\\\\"))) {
                    // 取出所有数据
                    append(rs);
                    this.first();
                    this.setActive(true);
                }
            }
        } catch (Exception e) {
            log.error(sql);
            throw new RuntimeException(e.getMessage());
        }
    }

    // 追加相同数据表的其它记录，与已有记录合并
    public final int attach(String sql) {
        if (!this.isActive()) {
            this.clear();
            this.add(sql);
            this.open();
            return this.size();
        }

        log.debug(sql.replaceAll("\r\n", " "));
        try (ConnectionClient client = getConnectionClient()) {
            try (Statement st = client.getConnection().createStatement()) {
                try (ResultSet rs = st.executeQuery(sql.replace("\\", "\\\\"))) {
                    int oldSize = this.size();
                    append(rs);
                    return this.size() - oldSize;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public final void save() {
        if (!this.isBatchSave())
            throw new RuntimeException("batchSave is false");
        ConnectionClient client = null;
        try {
            if (this.isStorage())
                client = getConnectionClient();
            // 先执行删除
            for (Record record : delList) {
                doBeforeDelete(record);
                if (this.isStorage())
                    getOperator().delete(client.getConnection(), record);
                doAfterDelete(record);
            }
            // 再执行增加、修改
            this.first();
            while (this.fetch()) {
                Record record = this.getCurrent();
                if (record.getState().equals(RecordState.dsInsert)) {
                    doBeforePost(record);
                    if (this.isStorage())
                        getOperator().insert(client.getConnection(), record);
                    doAfterPost(record);
                } else if (record.getState().equals(RecordState.dsEdit)) {
                    doBeforePost(record);
                    if (this.isStorage())
                        getOperator().update(client.getConnection(), record);
                    doAfterPost(record);
                }
            }
            delList.clear();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                client = null;
            }
        }
    }

    private void append(ResultSet rs) throws SQLException {
        // 取得字段清单
        ResultSetMetaData meta = rs.getMetaData();
        FieldDefs defs = this.getFieldDefs();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String field = meta.getColumnLabel(i);
            if (!defs.exists(field))
                defs.add(field, FieldType.Storage);
        }
        // 取得所有数据
        int total = this.size();
        while (rs.next()) {
            total++;
            if (this.getMaximum() > -1 && this.getMaximum() < total) {
                setFetchFinish(false);
                break;
            }
            Record record = this.newRecord();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String fn = rs.getMetaData().getColumnLabel(i);
                record.setField(fn, rs.getObject(fn));
            }
            record.setState(RecordState.dsNone);
            this.getRecords().add(record);
            this.last();
        }
        BigdataException.check(this, this.size());
    }

    @Override
    protected final void insertStorage(Record record) throws Exception {
        try (ConnectionClient client = getConnectionClient()) {
            getOperator().insert(client.getConnection(), record);
        }
    }

    @Override
    protected final void updateStorage(Record record) throws Exception {
        try (ConnectionClient client = getConnectionClient()) {
            getOperator().update(client.getConnection(), record);
        }
    }

    @Override
    protected final void deleteStorage(Record record) throws Exception {
        try (ConnectionClient client = getConnectionClient()) {
            getOperator().delete(client.getConnection(), record);
        }
    }

    /**
     * 注意：必须使用try finally结构！！！
     * 
     * @return 返回 ConnectionClient 接口对象
     */
    private final ConnectionClient getConnectionClient() {
        return (ConnectionClient) getServer().getClient();
    }

    public final SqlOperator getOperator() {
        if (operator == null)
            operator = getServer().getDefaultOperator(this);
        if (operator.getTableName() == null) {
            String sql = this.getSqlText().getText();
            if (sql != null)
                operator.setTableName(SqlText.findTableName(sql));
        }
        return operator;
    }

    public final void setOperator(SqlOperator operator) {
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

    /**
     * 增加sql指令内容，调用此函数需要自行解决sql注入攻击！
     *
     * @param sql 要增加的sql指令内容
     * @return 返回对象本身
     */
    public final SqlQuery add(String sql) {
        sqlText.add(sql);
        return this;
    }

    public final SqlQuery add(String format, Object... args) {
        sqlText.add(format, args);
        return this;
    }

    public final String getCommandText() {
        return this.sqlText.getText();
    }

    public final SqlText getSqlText() {
        return this.sqlText;
    }

    protected final void setSqlText(SqlText sqlText) {
        this.sqlText = sqlText;
    }

    public final boolean isActive() {
        return active;
    }

    private final void setActive(boolean value) {
        this.active = value;
    }

    public final void clear() {
        close();
        this.getSqlText().clear();
    }

    public final int getMaximum() {
        return getSqlText().getMaximum();
    }

    public final SqlQuery setMaximum(int maximum) {
        getSqlText().setMaximum(maximum);
        return this;
    }

    public final boolean isFetchFinish() {
        return fetchFinish;
    }

    protected final void setFetchFinish(boolean fetchFinish) {
        this.fetchFinish = fetchFinish;
    }

    protected abstract SqlServer getServer();

}
