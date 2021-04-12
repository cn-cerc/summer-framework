package cn.cerc.mis.sync;

import cn.cerc.core.Record;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

public class SyncTable {

    public static void append(String tableCode, Record record) {
        Record rs = new Record();
        rs.setField("__table", tableCode);
        rs.setField("__opera", "append");
        rs.copyValues(record);

        String buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
        try (Jedis jedis = SyncPushRedis.getJedis()) {
            jedis.lpush(buffKey, rs.toString());
        }
    }

    public static void delete(String tableCode, Record record) {
        Record rs = new Record();
        rs.setField("__table", tableCode);
        rs.setField("__opera", "delete");
        rs.copyValues(record);

        String buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
        try (Jedis jedis = SyncPushRedis.getJedis()) {
            jedis.lpush(buffKey, rs.toString());
        }
    }

    public static void update(String tableCode, Record record) {
        Record rs = new Record();
        rs.setField("__table", tableCode);
        rs.setField("__opera", "update");
        rs.copyValues(record);

        String buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
        try (Jedis jedis = SyncPushRedis.getJedis()) {
            jedis.lpush(buffKey, rs.toString());
        }
    }
}
