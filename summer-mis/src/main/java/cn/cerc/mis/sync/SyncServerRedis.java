package cn.cerc.mis.sync;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.redis.JedisFactory;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.core.SystemBuffer.SyncServer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

public class SyncServerRedis implements ISyncServer {
    private SyncServer pushQueue;
    private SyncServer popQueue;

    @Override
    public void push(ISession session, Record record) {
        if (pushQueue == null)
            throw new RuntimeException("pushQueue is null");
        String buffKey = MemoryBuffer.buildKey(pushQueue);
        String configKey = "sync." + pushQueue.name().toLowerCase();
        try (Jedis jedis = JedisFactory.getJedis(configKey)) {
            // 从左边推入
            jedis.lpush(buffKey, record.toString());
        }
    }

    @Override
    public void repush(ISession session, Record record) {
        if (pushQueue == null)
            throw new RuntimeException("pushQueue is null");
        String buffKey = MemoryBuffer.buildKey(pushQueue);
        String configKey = "sync." + pushQueue.name().toLowerCase();
        try (Jedis jedis = JedisFactory.getJedis(configKey)) {
            // 从右边推入
            jedis.rpush(buffKey, record.toString());
        }
    }

    @Override
    public int pop(ISession session, IPopProcesser popProcesser, int maxRecords) {
        if (popQueue == null)
            throw new RuntimeException("popQueue is null");
        String buffKey = MemoryBuffer.buildKey(popQueue);
        String configKey = "sync." + popQueue.name().toLowerCase();
        try (Jedis jedis = JedisFactory.getJedis(configKey)) {
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

    public SyncServer getPushQueue() {
        return pushQueue;
    }

    public void setPushQueue(SyncServer pushQueue) {
        this.pushQueue = pushQueue;
    }

    public SyncServer getPopQueue() {
        return popQueue;
    }

    public void setPopQueue(SyncServer popQueue) {
        this.popQueue = popQueue;
    }

}
