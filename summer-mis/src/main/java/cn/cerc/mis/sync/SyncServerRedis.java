package cn.cerc.mis.sync;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

public class SyncServerRedis implements ISyncServer {
    private String buffKey;

    public SyncServerRedis() {
        buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
    }

    @Override
    public void push(ISession session, Record record) {
        try (Jedis jedis = SyncPushRedis.getJedis()) {
            // 从左边推入
            jedis.lpush(buffKey, record.toString());
        }
    }

    @Override
    public void repush(ISession session, Record record) {
        try (Jedis jedis = SyncPullRedis.getJedis()) {
            // 从右边推入
            jedis.rpush(buffKey, record.toString());
        }
    }

    @Override
    public int pop(ISession session, IPopProcesser popProcesser, int maxRecords) {
        try (Jedis jedis = SyncPullRedis.getJedis()) {
            for (int i = 0; i < maxRecords; i++) {
                // 从右边取出
                String data = jedis.rpop(buffKey);
                if (data == null) {
                    return i;
                }

                Record record = new Record();
                record.setJSON(data);
                popProcesser.popRecord(session, record);
            }
        }
        return maxRecords;
    }

}
