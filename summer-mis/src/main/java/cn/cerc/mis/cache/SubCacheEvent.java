package cn.cerc.mis.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.mis.core.BasicHandle;
import redis.clients.jedis.JedisPubSub;

public class SubCacheEvent extends JedisPubSub {
    private static final Logger log = LoggerFactory.getLogger(SubCacheEvent.class);
    
    @Override
    public void onMessage(String channel, String message) {
        if (!MemoryListener.CacheChannel.equals(channel)) {
            log.warn("not support command: {}", message);
            return;
        }
        try {
            String args[] = message.split(":");
            String beanId = args[0];
            if (MemoryListener.context.containsBean(beanId) && MemoryListener.context.isSingleton(beanId)) {
                String param = null;
                if (args.length > 1)
                    param = message.substring(beanId.length() + 1);
                log.debug("{}.resetCache:{}", beanId, param);
                IMemoryCache bean = MemoryListener.context.getBean(beanId, IMemoryCache.class);
                try (BasicHandle handle = new BasicHandle()) {
                    bean.resetCache(handle, CacheResetMode.Update, param);
                }
            } else {
                log.warn("not find beanId: {}", message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }
}
