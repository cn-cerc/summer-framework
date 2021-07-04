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
import cn.cerc.core.DataSetEvent;
import cn.cerc.core.FieldDefs;
import cn.cerc.core.Record;
import cn.cerc.core.RecordState;
import cn.cerc.core.SqlText;

@SuppressWarnings("serial")
public abstract class SqlQuery extends DataSet {
    private static final Logger log = LoggerFactory.getLogger(SqlQuery.class);
    // 数据集是否有打开
    private boolean active = false;
    // 若数据有取完，则为true，否则为false
    private boolean fetchFinish;
    // 使用只读数据源
    protected boolean slaveServer;
    // 仅当batchSave为true时，delList才有记录存在
    protected List<Record> delList = new ArrayList<>();
    // 数据库保存操作执行对象
    private SqlOperator operator;
    // SqlCommand 指令
    private SqlText sqlText = new SqlText();

    @Override
    public final void close() {
        this.setActive(false);
        this.operator = null;
        super.close();
    }

    public final SqlQuery open() {
        this.setStorage(true);
        open(false);
        return this;
    }

    public final SqlQuery openReadonly() {
        this.setStorage(false);
        open(true);
        return this;
    }

    private final void open(boolean slaveServer) {
        this.setSlaveServer(slaveServer);
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

    protected void append(ResultSet rs) throws SQLException {
        DataSetEvent afterAppend = this.getOnAppend();
        try {
            this.onAppend(null);
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
                this.append(record);
            }
            BigdataException.check(this, this.size());
        } finally {
            this.onAppend(afterAppend);
        }
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

    public final SqlOperator getOperator() {
        if (operator == null)
            operator = getDefaultOperator();
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

    public final void setSqlText(SqlText sqlText) {
        this.sqlText = sqlText;
    }

    public final boolean isActive() {
        return active;
    }

    protected void setActive(boolean value) {
        this.active = value;
    }

    public final boolean isSlaveServer() {
        return slaveServer;
    }

    public final void setSlaveServer(boolean slaveServer) {
        this.slaveServer = slaveServer;
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

    /**
     * 注意：必须使用try finally结构！！！
     * 
     * @return 返回 ConnectionClient 接口对象
     */
    protected abstract ConnectionClient getConnectionClient();

    protected abstract SqlOperator getDefaultOperator();

}
