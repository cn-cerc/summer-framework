package cn.cerc.sms;

import org.junit.Before;
import org.junit.Test;

public class YunpianSMSTest {

    private YunpianSMS yunpian;

    @Before
    public void setUp() {
        yunpian = new YunpianSMS("4cf5608b2be57c8275ab67b9c1710797");
    }

    @Test
    public void test() {
        yunpian.send("15915438774", "【万事达】您的验证码是87616");
    }

}
