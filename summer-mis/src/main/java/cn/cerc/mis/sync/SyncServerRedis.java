package cn.cerc.mis.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.redis.JedisFactory;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.core.SystemBuffer.SyncServer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

public class SyncServerRedis implements ISyncServer {
    private static final Logger log = LoggerFactory.getLogger(SyncServerRedis.class);

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
        if (popQueue == null)
            throw new RuntimeException("popQueue is null");
        String buffKey = MemoryBuffer.buildKey(popQueue);
        String configKey = "sync." + popQueue.name().toLowerCase();
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
                try {
                    popProcesser.popRecord(session, record);
                } catch (Exception e) {
                    log.error(record.toString());
                    e.printStackTrace();
                }
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
