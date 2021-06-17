package cn.cerc.mis.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.redis.JedisFactory;
import cn.cerc.mis.core.SystemBuffer.SyncServer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

public class SyncServerRedis implements ISyncServer {

    private static final Logger log = LoggerFactory.getLogger(SyncServerRedis.class);

    private SyncServer pushFrom;
    private SyncServer pushTo;

    private SyncServer popFrom;
    private SyncServer popTo;

    public void initPushQueue(SyncServer pushFrom, SyncServer pushTo) {
        this.pushFrom = pushFrom;
        this.pushTo = pushTo;
    }

    public void initPopQueue(SyncServer popFrom, SyncServer popTo) {
        this.popFrom = popFrom;
        this.popTo = popTo;
    }

    /**
     * 从左边推入
     */
    @Override
    public void push(ISession session, Record record) {
        if (pushFrom == null)
            throw new RuntimeException("pushFrom is null");
        if (pushTo == null)
            throw new RuntimeException("pushTo is null");

        String buffKey = MemoryBuffer.buildKey(pushFrom);
        String configKey = "sync." + pushFrom.name().toLowerCase() + "-to-" + pushTo.name().toLowerCase();
        try (Jedis jedis = JedisFactory.getJedis(configKey)) {
            jedis.lpush(buffKey, record.toString());
        }
    }

    /**
     * 从右边推入
     */
    @Override
    public void repush(ISession session, Record record) {
        if (popFrom == null)
            throw new RuntimeException("popFrom is null");
        if (popTo == null)
            throw new RuntimeException("popTo is null");

        String buffKey = MemoryBuffer.buildKey(popFrom);
        String configKey = "sync." + popFrom.name().toLowerCase() + "-to-" + popTo.name().toLowerCase();
        try (Jedis jedis = JedisFactory.getJedis(configKey)) {
            jedis.rpush(buffKey, record.toString());
        }
    }

    /**
     * 从右边取出
     */
    @Override
    public int pop(ISession session, IPopProcesser popProcesser, int maxRecords) {
        if (popFrom == null)
            throw new RuntimeException("popFrom is null");
        if (popTo == null)
            throw new RuntimeException("popTo is null");

        String buffKey = MemoryBuffer.buildKey(popFrom);
        String configKey = "sync." + popFrom.name().toLowerCase() + "-to-" + popTo.name().toLowerCase();

        try (Jedis jedis = JedisFactory.getJedis(configKey)) {
            for (int i = 0; i < maxRecords; i++) {
                String data = jedis.rpop(buffKey);
                if (data == null) {
                    return i;
                }

                Record record = new Record();
                record.setJSON(data);
                try {
                    popProcesser.popRecord(session, record, false);
                } catch (Exception e) {
                    log.error(record.toString());
                    e.printStackTrace();
                }
            }
        }
        return maxRecords;
    }

}
