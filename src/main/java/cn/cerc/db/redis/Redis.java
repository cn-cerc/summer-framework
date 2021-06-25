package cn.cerc.db.redis;

import redis.clients.jedis.Jedis;

public class Redis {

    public static String get(String key) {
        try (Jedis jedis = JedisFactory.getJedis()) {
            return jedis != null ? jedis.get(key) : null;
        }
    }

    public static void set(String key, Object value) {
        set(key, value, 3600);
    }

    public static void set(String key, Object value, int expires) {
        try (Jedis jedis = JedisFactory.getJedis()) {
            if (jedis != null) {
                if (value instanceof String) {
                    jedis.set(key, (String) value);
                } else {
                    jedis.set(key, value.toString());
                }
                jedis.expire(key, expires);
            }
        }
    }

    public static void delete(String key) {
        try (Jedis jedis = JedisFactory.getJedis()) {
            if (jedis != null)
                jedis.del(key);
        }
    }
}
