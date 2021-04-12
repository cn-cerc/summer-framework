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

public abstract class SyncProcess implements ISessionOwner {
    private static final Logger log = LoggerFactory.getLogger(SyncProcess.class);
    private ISession session;

    @Override
    public ISession getSession() {
        return this.session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    public void execute() {
        String buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
        try (Jedis jedis = SyncPullRedis.getJedis()) {
            while (true) {
                String data = jedis.rpop(buffKey);
                if (data == null)
                    break;

                Record record = new Record();
                record.setJSON(data);

                String tableCode = record.getString("__table");
                String opera = record.getString("__opera");
                int error = record.getInt("__error");
                record.delete("__table");
                record.delete("__opera");
                record.delete("__error");
                boolean result = false;
                if ("append".equals(opera)) {
                    result = sync(tableCode, record, SyncOpera.Append);
                } else if ("delete".equals(opera)) {
                    result = sync(tableCode, record, SyncOpera.Delete);
                } else if ("update".equals(opera)) {
                    result = sync(tableCode, record, SyncOpera.Update);
                } else {
                    throw new RuntimeException("opera not support: " + opera);
                }
                if (!result) {
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

    public boolean sync(String tableCode, Record record, SyncOpera opera) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select * from %s", tableCode);
        ds.add("where UID_='%s'", record.getString("UID_"));
        ds.open();

        ISyncRecord sync = Application.getBean(ISyncRecord.class, "Sync_" + tableCode);
        switch (opera) {
        case Append:
            if (!ds.eof())
                return false;
            if (sync != null && !sync.onAppend(record))
                return false;
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
        default:
            throw new RuntimeException("not support opera.");
        }
        return true;
    };

}
