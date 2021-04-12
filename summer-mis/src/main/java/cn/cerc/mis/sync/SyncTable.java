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
                if (!process(tableCode, record, SyncOpera.values()[opera])) {
                    if (error < 5) {
                        record.setField("__table", tableCode);
                        record.setField("__opera", opera);
                        record.setField("__error", error + 1);
                        jedis.rpush(buffKey, record.toString());
                        log.error("sync error: {}.{}", tableCode, opera);
                    }
                }
            }
        }
    }

    private boolean process(String tableCode, Record record, SyncOpera opera) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_='%s'", record.getString("UID_"));
        ds.open();

        ISyncRecord sync = Application.getBean(ISyncRecord.class, "sync_" + tableCode);
        if (sync != null && sync instanceof ISessionOwner) {
            ((ISessionOwner) sync).setSession(this.getSession());
        }
        switch (opera) {
        case Append:
            if (!ds.eof())
                return false;
            if (sync != null && !sync.onAppend(record))
                return false;
            ds.getDefaultOperator().setUpdateKey("");
            ds.append();
            ds.copyRecord(record, ds.getFieldDefs());
            ds.post();
            break;
        case Delete:
            if (ds.eof())
                return false;
            if (sync != null && !sync.onDelete(ds.getCurrent()))
                return false;
            ds.delete();
            break;
        case Update:
            if (ds.eof())
                return false;
            if (sync != null && !sync.onUpdate(ds.getCurrent(), record))
                return false;
            ds.edit();
            ds.copyRecord(record, ds.getFieldDefs());
            ds.post();
            break;
        case Reset:
            if (ds.eof()) {
                if (sync != null && !sync.onAppend(record))
                    return false;
                ds.getDefaultOperator().setUpdateKey("");
                ds.append();
            } else {
                if (sync != null && !sync.onUpdate(ds.getCurrent(), record))
                    return false;
                ds.edit();
            }
            ds.copyRecord(record, ds.getFieldDefs());
            ds.post();
            break;
        default:
            throw new RuntimeException("not support opera.");
        }
        return true;
    };

}
