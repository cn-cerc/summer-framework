package cn.cerc.mis.core;

import cn.cerc.db.cache.Redis;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MemcachedTest {

    @Test
    @Ignore
    public void test() {
        String buffKey = "test";
        String value = "OK!";
        Redis.set(buffKey, value, 2);

        Object buffData;
        for (int i = 1; i < 5; i++) {
            buffData = Redis.get(buffKey);
            String msg = String.format("第 %d 次测试", i);
            assertEquals(msg, i <= 2 ? value : null, buffData);
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1005);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
