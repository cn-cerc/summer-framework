package cn.cerc.db.core;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurlTest {
    private static final Logger log = LoggerFactory.getLogger(CurlTest.class);

    @Test
    public void test() {
        String host = "http://smapi.sanmaoyou.com/api/tips/list/v2";
//        String host = "https://www.jayun.site/api/message";

        Curl curl = new Curl();
        log.info(curl.sendGet(host));
    }

    // @Test
    public void test_param() {
//        https://tf.sanmaoyou.com/api/tips/list/v2?utm_tid=126&city_id=&country_id=5043&keyword=&page=1&page_size=10

        Curl curl = new Curl();
        curl.put("utm_tid", 126);
        curl.put("country_id", 5043);

        String host = "http://smapi.sanmaoyou.com/api/tips/list/v2";
        log.info(curl.sendGet(host));
    }

}
