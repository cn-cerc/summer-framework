package cn.cerc.mis.sync;

import cn.cerc.core.Record;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

public class SyncQueueRedis implements ISyncQueue {
    private String buffKey;

    public SyncQueueRedis() {
        buffKey = MemoryBuffer.buildKey(SystemBuffer.Global.SyncDatabase);
    }

    @Override
    public void push(Record record) {
        try (Jedis jedis = SyncPushRedis.getJedis()) {
            // 从左边推入
            jedis.lpush(buffKey, record.toString());
        }
    }

    @Override
    public Record pop() {
        try (Jedis jedis = SyncPullRedis.getJedis()) {
            // 从右边取出
            String data = jedis.rpop(buffKey);
            if (data == null) {
                return null;
            }

            Record record = new Record();
            record.setJSON(data);
            return record;
        }
    }

    @Override
    public void repush(Record record) {
        try (Jedis jedis = SyncPullRedis.getJedis()) {
            // 从右边推入
            jedis.rpush(buffKey, record.toString());
        }
    }

}
