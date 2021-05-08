package cn.cerc.mis.cache;

import cn.cerc.db.redis.JedisFactory;
import redis.clients.jedis.Jedis;

public class CacheResetMonitor extends Thread {
        private SubCacheEvent monitor = new SubCacheEvent();

        @Override
        public void run() {
            try (Jedis jedis = JedisFactory.getJedis()) {
                if (jedis != null)
                    jedis.subscribe(monitor, MemoryListener.CacheChannel);
            }
        }

        public void requestStop() {
            if (monitor.isSubscribed())
                monitor.unsubscribe();
        }
    }