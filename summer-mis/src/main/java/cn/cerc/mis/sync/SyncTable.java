package cn.cerc.mis.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.core.ISessionOwner;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

public class SyncTable implements ISessionOwner {
    private static final Logger log = LoggerFactory.getLogger(SyncTable.class);
    private ISession session;

    @Override
    public ISession getSession() {
        return this.session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    public static void push(String tableCode, Record record, SyncOpera opera) {
        Record rs = new Record();
        rs.setField("__table", tableCode);
        rs.setField("__opera", opera.ordinal());
        rs.copyValues(record);

        String buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
        try (Jedis jedis = SyncPushRedis.getJedis()) {
            jedis.lpush(buffKey, rs.toString());
        }
    }

    public void pull(int times) {
        String buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
        try (Jedis jedis = SyncPullRedis.getJedis()) {
            for (int i = 0; i < times; i++) {
                String data = jedis.rpop(buffKey);
                if (data == null) {
                    continue;
                }

                Record record = new Record();
                record.setJSON(data);

                String tableCode = record.getString("__table");
                int opera = record.getInt("__opera");
                int error = record.getInt("__error");
                record.delete("__table");
                record.delete("__opera");
                record.delete("__error");

                boolean result;
                switch (SyncOpera.values()[opera]) {
                case Append:
                    result = appendRecord(tableCode, record);
                    break;
                case Delete:
                    result = deleteRecord(tableCode, record);
                    break;
                case Update:
                    result = updateRecord(tableCode, record);
                    break;
                case Reset:
                    result = resetRecord(tableCode, record);
                    break;
                default:
                    throw new RuntimeException("not support opera.");
                }

                if (!result) {
                    record.setField("__table", tableCode);
                    record.setField("__opera", opera);
                    record.setField("__error", error + 1);
                    if (error < 5) {
                        jedis.rpush(buffKey, record.toString());
                        log.error("sync {}.{} fail, times {}", tableCode, opera, error);
                    } else {
                        abortRecord(tableCode, record, SyncOpera.values()[opera]);
                    }
                }
            }
        }
    }

    protected boolean appendRecord(String tableCode, Record record) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_='%s'", record.getString("UID_"));
        ds.open();
        if (!ds.eof())
            return false;

        ISyncEvent sync = Application.getBean(ISyncEvent.class, "sync_" + tableCode);
        if (sync != null) {
            if (sync instanceof ISessionOwner) {
                ((ISessionOwner) sync).setSession(this.getSession());
            }
            if (!sync.onAppend(record)) {
                return false;
            }
        }

        ds.getDefaultOperator().setUpdateKey("");
        ds.append();
        ds.copyRecord(record, ds.getFieldDefs());
        ds.post();

        return true;
    }

    protected boolean deleteRecord(String tableCode, Record record) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_='%s'", record.getString("UID_"));
        ds.open();
        if (ds.eof())
            return false;

        ISyncEvent sync = Application.getBean(ISyncEvent.class, "sync_" + tableCode);
        if (sync != null) {
            if (sync instanceof ISessionOwner) {
                ((ISessionOwner) sync).setSession(this.getSession());
            }
            if (!sync.onDelete(ds.getCurrent()))
                return false;
        }

        ds.delete();
        return true;
    }

    protected boolean updateRecord(String tableCode, Record record) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_='%s'", record.getString("UID_"));
        ds.open();
        if (ds.eof())
            return false;

        ISyncEvent sync = Application.getBean(ISyncEvent.class, "sync_" + tableCode);
        if (sync != null) {
            if (sync instanceof ISessionOwner) {
                ((ISessionOwner) sync).setSession(this.getSession());
            }
            if (!sync.onUpdate(ds.getCurrent(), record))
                return false;
        }

        ds.edit();
        ds.copyRecord(record, ds.getFieldDefs());
        ds.post();
        return true;
    }

    protected boolean resetRecord(String tableCode, Record record) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_='%s'", record.getString("UID_"));
        ds.open();

        ISyncEvent sync = Application.getBean(ISyncEvent.class, "sync_" + tableCode);
        if (sync != null && sync instanceof ISessionOwner) {
            ((ISessionOwner) sync).setSession(this.getSession());
        }

        if (ds.eof()) {
            if (sync != null && !sync.onAppend(record))
                return false;
            ds.getDefaultOperator().setUpdateKey("");
            ds.append();
            ds.copyRecord(record, ds.getFieldDefs());
            ds.post();
        } else {
            if (sync != null && !sync.onUpdate(ds.getCurrent(), record))
                return false;
            ds.edit();
            ds.copyRecord(record, ds.getFieldDefs());
            ds.post();
        }
        return true;
    }

    protected void abortRecord(String tableCode, Record record, SyncOpera opera) {
        log.error("sync {}.{} abort.", tableCode, SyncOpera.getName(opera));
    }

}
