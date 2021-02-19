package cn.cerc.mis.sms;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YunpianSMSTest {
    private static final Logger log = LoggerFactory.getLogger(YunpianSMSTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        YunpianSMS sms = new YunpianSMS("13826575465");
        String text = "【MIUGROUP】您的验证码是123456";
        if (sms.sendText(text)) {
            log.info("ok: ");
            log.info(sms.getMessage());
        } else {
            log.info("error: ");
            log.info(sms.getMessage());
        }
    }

}
