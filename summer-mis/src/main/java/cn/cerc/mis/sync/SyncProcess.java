package cn.cerc.mis.sync;

import cn.cerc.core.Record;
import cn.cerc.db.redis.JedisFactory;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

public class SyncProcess {
    private SyncRecord syncAppend;
    private SyncRecord syncDelete;
    private SyncRecord syncUpdate;

    public void execute() {
        String buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
        try (Jedis jedis = JedisFactory.getJedis()) {
            String data = jedis.rpop(buffKey);
            Record record = new Record();
            record.setJSON(data);
            
            String tableCode = record.getString("__table");
            String opera = record.getString("__opera");
            record.delete("__table");
            record.delete("__opera");
            if ("append".equals(opera)) {
                if (syncAppend != null) {
                    syncAppend.execute(tableCode, record);
                }
            } else if ("delete".equals(opera)) {
                if (syncDelete != null) {
                    syncDelete.execute(tableCode, record);
                }
            } else if ("update".equals(opera)) {
                if (syncUpdate != null) {
                    syncUpdate.execute(tableCode, record);
                }
            } else {
                throw new RuntimeException("opera not support: " + opera);
            }
        }
    }

    public interface SyncRecord {
        void execute(String tableCode, Record record);
    }

    public void defineAppend(SyncRecord syncAppend) {
        this.syncAppend = syncAppend;
    }

    public void defineDelete(SyncRecord syncDelete) {
        this.syncDelete = syncDelete;
    }

    public void defineUpdate(SyncRecord syncUpdate) {
        this.syncUpdate = syncUpdate;
    }
}
