package cn.cerc.mis.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.mysql.SqlQuery;

public class SyncTableDefault implements ISyncRecord {
    private static final Logger log = LoggerFactory.getLogger(SyncTableDefault.class);
    private ISession session;
    private String tableCode;

    @Override
    public boolean appendRecord(Record record) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_=%d", record.getInt("UID_"));
        ds.open();
        if (!ds.eof())
            return false;
        if (!this.onAppend(record))
            return false;
        ds.getDefaultOperator().setUpdateKey("");
        ds.append();
        ds.copyRecord(record, ds.getFieldDefs());
        ds.post();

        return true;
    }

    @Override
    public boolean deleteRecord(Record record) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_=%d", record.getInt("UID_"));
        ds.open();
        if (ds.eof())
            return false;

        if (!this.onDelete(ds.getCurrent()))
            return false;

        ds.delete();
        return true;
    }

    @Override
    public boolean updateRecord(Record record) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_=%d", record.getInt("UID_"));
        ds.open();
        if (ds.eof())
            return false;

        if (!this.onUpdate(ds.getCurrent(), record))
            return false;

        ds.edit();
        ds.copyRecord(record, ds.getFieldDefs());
        ds.post();
        return true;
    }

    @Override
    public boolean resetRecord(Record record) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_=%d", record.getInt("UID_"));
        ds.open();

        if (ds.eof()) {
            if (!this.onAppend(record))
                return false;
            ds.getDefaultOperator().setUpdateKey("");
            ds.append();
            ds.copyRecord(record, ds.getFieldDefs());
            ds.post();
        } else {
            if (!this.onUpdate(ds.getCurrent(), record))
                return false;
            ds.edit();
            ds.copyRecord(record, ds.getFieldDefs());
            ds.post();
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

    public SyncTableDefault setTableCode(String tableCode) {
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
