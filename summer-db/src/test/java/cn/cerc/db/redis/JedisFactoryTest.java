package cn.cerc.db.redis;

import org.junit.Ignore;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class JedisFactoryTest {

    @Test
    @Ignore
    public void test() {
        try (Jedis jedis = JedisFactory.getJedis()) {
            jedis.set("test", "hello");
            System.out.println(jedis.get("test"));
        }
    }

}
