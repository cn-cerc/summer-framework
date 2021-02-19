package cn.cerc.mis.sms;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.mis.rds.StubHandle;

public class PhoneVerifyTest {
    private static final Logger log = LoggerFactory.getLogger(PhoneVerifyTest.class);

    private StubHandle handle;
    private PhoneVerify obj;
    private String mobile = "13543762702";

    @Before
    public void setUp() throws Exception {
        handle = new StubHandle();
        obj = new PhoneVerify(handle, mobile);
    }

    @Test
    public void test_sendSMS() {
        boolean result = obj.sendMessage("1234");
        log.info(obj.getMessage());
        assertEquals(result, true);
    }

    @Test
    @Ignore
    public void test_1() {
        // 清空缓存
        obj.clearBuffer();
        // 取验证码
        assertEquals(obj.readVerifyCode(), false);
        assertEquals(obj.getMessage(), PhoneVerify.ERROR_2);
        // 发送验证码
        assertEquals(obj.sendVerifyCode(), true);
        // 检验验证码
        String tmp = obj.getVerifyCode();
        assertEquals(obj.readVerifyCode(), true);
        assertEquals(tmp, obj.getVerifyCode());
    }

    @Test
    @Ignore
    public void test_2() {
        obj.setExpires(1);
        // 清空缓存
        obj.clearBuffer();
        // 发送验证码
        assertEquals(obj.sendVerifyCode(), true);
        log.info("第一次发送：" + obj.getVerifyCode());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(obj.sendVerifyCode(), false);
        assertEquals(obj.getMessage(), PhoneVerify.ERROR_1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(obj.sendVerifyCode(), true);
        log.info("第二次发送：" + obj.getVerifyCode());
    }
}
