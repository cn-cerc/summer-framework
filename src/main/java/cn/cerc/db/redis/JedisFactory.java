package cn.cerc.db.redis;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Utils;
import cn.cerc.db.SummerDB;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisFactory {
    private static final Logger log = LoggerFactory.getLogger(JedisFactory.class);
    private static final ClassConfig config = new ClassConfig(JedisFactory.class, SummerDB.ID);
    private static final Map<String, JedisFactory> items = new HashMap<>();

    // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static final int MAX_ACTIVE = 1024;
    // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static final int MAX_IDLE = 200;
    // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static final int MAX_WAIT = 10000;
    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static final boolean TEST_ON_BORROW = true;

    // redis pool
    private JedisPool jedisPool = null;
    private String host;
    private int port;
    private int error = 0;

    /**
     * 创建默认的 RedisServer
     * 
     * @return JedisFactory
     */
    public static JedisFactory create() {
        return create(null);
    }

    /**
     * 创建 RedisServer
     *
     * @param configId 用于在配置文件中区分不同的redis服务器的连接参数，取值如：sync，若为 null 则返回缺省配置
     * 
     * @return JedisFactory
     */
    public static JedisFactory create(String configId) {
        if (items.containsKey(configId)) {
            return items.get(configId);
        }
        synchronized (JedisFactory.class) {
            JedisFactory item = new JedisFactory(configId);
            items.put(configId, item);
            return item;
        }
    }

    /**
     * 返回 RedisServer 的 Jedis
     *
     * @param configId 用于在配置文件中区分不同的redis服务器的连接参数，取值如：sync，若为 null 则返回缺省配置
     * 
     * @return Jedis
     */
    public static Jedis getJedis(String configId) {
        return create(configId).getResource();
    }

    /**
     * 返回默认RedisServer的Jedis
     * 
     * @return Jedis
     */
    public static Jedis getJedis() {
        return create(null).getResource();
    }

    private JedisFactory(String configId) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(MAX_ACTIVE);
        poolConfig.setMaxIdle(MAX_IDLE);
        poolConfig.setMaxWaitMillis(MAX_WAIT);
        poolConfig.setTestOnBorrow(TEST_ON_BORROW);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        // Idle时进行连接扫描
        poolConfig.setTestWhileIdle(true);
        // 表示idle object evitor两次扫描之间要sleep的毫秒数
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        // 表示idle object evitor每次扫描的最多的对象数
        poolConfig.setNumTestsPerEvictionRun(10);
        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
        // evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        poolConfig.setMinEvictableIdleTimeMillis(60000);

        String extKey = "";
        if (configId == null) {
            this.host = config.getString("redis.host", "127.0.0.1");
            this.port = config.getInt("redis.port", 6379);
        } else {
            extKey = "." + configId;
            this.host = config.getString("redis.host" + extKey, null);
            this.port = config.getInt("redis.port" + extKey, 0);
        }

        if (Utils.isEmpty(host)) {
            log.error("redis.host{} not config.", extKey);
            return;
        }

        String password = config.getString("redis.password" + extKey, null);
        int timeout = config.getInt("redis.timeout" + extKey, 10000);

        // 建立连接池
        log.info("redis server {}:{} starting", host, port);
        jedisPool = new JedisPool(poolConfig, host, port, timeout, password);
    }

    public Jedis getResource() {
        if (jedisPool == null) {
            log.error("redis server {}:{} not exist.", host, port);
            return null;
        }
        // 达3次时，不再重试
        if (error >= 3) {
            return null;
        }
        try {
            return jedisPool.getResource();
        } catch (JedisConnectionException e) {
            if (error < 3) {
                log.error("redis server not run.");
                error++;
            }
            return null;
        }
    }

    public static void close() {
        items.values().forEach((jf) -> {
            jf.jedisPool.close();
        });
    }

}
