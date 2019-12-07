package cn.cerc.db.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.db.core.ServerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisFactory {
    private static final Logger log = LoggerFactory.getLogger(JedisFactory.class);
    public static final String redis_site = "redis.host";
    public static final String redis_port = "redis.port";
    public static final String redis_password = "redis.password";
    public static final String redis_timeout = "redis.timeout";

    // 可用连接实例的最大数目，默认值为8；
    // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 1024;
    // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;
    // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;
    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;
    // redis pool
    private static JedisPool jedisPool;

    static {
        JedisPoolConfig jconfig = new JedisPoolConfig();
        jconfig.setMaxTotal(MAX_ACTIVE);
        jconfig.setMaxIdle(MAX_IDLE);
        jconfig.setMaxWaitMillis(MAX_WAIT);
        jconfig.setTestOnBorrow(TEST_ON_BORROW);
        jconfig.setTestOnBorrow(true);
        jconfig.setTestOnReturn(true);
        // Idle时进行连接扫描
        jconfig.setTestWhileIdle(true);
        // 表示idle object evitor两次扫描之间要sleep的毫秒数
        jconfig.setTimeBetweenEvictionRunsMillis(30000);
        // 表示idle object evitor每次扫描的最多的对象数
        jconfig.setNumTestsPerEvictionRun(10);
        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
        // evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        jconfig.setMinEvictableIdleTimeMillis(60000);
        ServerConfig config = ServerConfig.getInstance();
        String host = config.getProperty(redis_site, "127.0.0.1");// ip
        int port = Integer.parseInt(config.getProperty(redis_port, "6379"));// 端口号
        String AUTH = config.getProperty(redis_password, null);// 密码
        if ("".equals(AUTH))
            AUTH = null;
        int TIMEOUT = Integer.parseInt(config.getProperty(redis_timeout, "10000")); // 超时
        log.info(String.format("redis server %s:%s", host, port));
        // 建立连接池
        jedisPool = new JedisPool(jconfig, host, port, TIMEOUT, AUTH);
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

}
