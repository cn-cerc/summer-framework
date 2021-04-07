package cn.cerc.db.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisFactory {
    private static final Logger log = LoggerFactory.getLogger(JedisFactory.class);

    public static final String redis_site = "redis.host";
    public static final String redis_port = "redis.port";
    public static final String redis_password = "redis.password";
    public static final String redis_timeout = "redis.timeout";

    // 可用连接实例的最大数目，默认值为8；

    // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static final int MAX_ACTIVE = 1024;

    // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static final int MAX_IDLE = 200;

    // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static final int MAX_WAIT = 10000;

    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static final boolean TEST_ON_BORROW = true;

    // redis pool
    private static final JedisPool jedisPool;

    static {
        JedisPoolConfig pool = new JedisPoolConfig();
        pool.setMaxTotal(MAX_ACTIVE);
        pool.setMaxIdle(MAX_IDLE);
        pool.setMaxWaitMillis(MAX_WAIT);
        pool.setTestOnBorrow(TEST_ON_BORROW);
        pool.setTestOnBorrow(true);
        pool.setTestOnReturn(true);

        // Idle时进行连接扫描
        pool.setTestWhileIdle(true);

        // 表示idle object evitor两次扫描之间要sleep的毫秒数
        pool.setTimeBetweenEvictionRunsMillis(30000);

        // 表示idle object evitor每次扫描的最多的对象数
        pool.setNumTestsPerEvictionRun(10);

        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
        // evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        pool.setMinEvictableIdleTimeMillis(60000);

        ClassConfig config = new ClassConfig();
        String host = config.getString(redis_site, "127.0.0.1");
        int port = config.getInt(redis_port, 6379);

        String password = config.getString(redis_password, null);
        if ("".equals(password)) {
            password = null;
        }

        int timeout = config.getInt(redis_timeout, 10000);
        log.info("redis server {}:{}", host, port);

        // 建立连接池
        jedisPool = new JedisPool(pool, host, port, timeout, password);
    }

    public static Jedis getJedis() {
        try {
            return jedisPool.getResource();
        } catch (JedisConnectionException e) {
            log.error("redis service not run.");
            return null;
        }
    }

}
