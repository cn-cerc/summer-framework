package cn.cerc.mis.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.mysql.SqlQuery;

public class PushTableDefault implements IPushProcesser {
    private static final Logger log = LoggerFactory.getLogger(PushTableDefault.class);
    private ISession session;
    private String tableCode;

    /**
     * 1-数据不存在
     * <p>
     * 数据已存在
     * <p>
     * 2 网络异常
     * <p>
     * 3-继承类编写错误
     * <p>
     * 4、参数设置错误
     * <p>
     * 5-不满足写入条件
     * <p>
     * 6-java异常
     * <p>
     */
    @Override
    public boolean appendRecord(Record record) {
        SqlQuery query = new SqlQuery(this);
        query.add("select * from %s", tableCode);
        query.add("where UID_=%d", record.getInt("UID_"));
        query.open();
        if (!query.eof()) {
            log.error("append error！table {}, uid {}, record {}", tableCode, record.getInt("UID_"), record);
            return false;
        }
        if (!this.onAppend(record))
            return false;
        query.getDefaultOperator().setUpdateKey("");
        query.append();
        query.copyRecord(record, query.getFieldDefs());
        query.post();
        return true;
    }

    @Override
    public boolean deleteRecord(Record record) {
        SqlQuery query = new SqlQuery(this);
        query.add("select * from %s", tableCode);
        query.add("where UID_=%d", record.getInt("UID_"));
        query.open();
        if (query.eof()) {
            log.error("delete error！table {}, uid {}, record {}", tableCode, record.getInt("UID_"), record);
            return false;
        }

        if (!this.onDelete(query.getCurrent()))
            return false;

        query.delete();
        return true;
    }

    @Override
    public boolean updateRecord(Record record) {
        SqlQuery query = new SqlQuery(this);
        query.add("select * from %s", tableCode);
        query.add("where UID_=%d", record.getInt("UID_"));
        query.open();
        if (query.eof()) {
            log.error("update error！ table {}, uid {}, record {}", tableCode, record.getInt("UID_"), record);
            return false;
        }

        if (!this.onUpdate(query.getCurrent(), record))
            return false;

        query.edit();
        query.copyRecord(record, query.getFieldDefs());
        query.post();
        return true;
    }

    @Override
    public boolean resetRecord(Record record) {
        SqlQuery query = new SqlQuery(this);
        query.add("select * from %s", tableCode);
        query.add("where UID_=%d", record.getInt("UID_"));
        query.open();

        if (query.eof()) {
            if (!this.onAppend(record))
                return false;
            query.getDefaultOperator().setUpdateKey("");
            query.append();
            query.copyRecord(record, query.getFieldDefs());
            query.post();
        } else {
            if (!this.onUpdate(query.getCurrent(), record))
                return false;
            query.edit();
            query.copyRecord(record, query.getFieldDefs());
            query.post();
        }
        return true;
    }

    @Override
    public void abortRecord(Record record, SyncOpera opera) {
        log.error("sync {}.{} abort.", tableCode, SyncOpera.getName(opera));
    }

    protected boolean onAppend(Record newRecord) {
        return true;
    }

    protected boolean onDelete(Record current) {
        return true;
    }

    protected boolean onUpdate(Record current, Record newRecord) {
        return true;
    }

    public String getTableCode() {
        return tableCode;
    }

    public PushTableDefault setTableCode(String tableCode) {
        this.tableCode = tableCode;
        return this;
    }

    @Override
    public ISession getSession() {
        return this.session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

}
