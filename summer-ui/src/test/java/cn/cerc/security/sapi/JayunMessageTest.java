package cn.cerc.security.sapi;

import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;

import org.junit.Ignore;
import org.junit.Test;

import security.sapi.JayunMessage;
import security.sapi.SendMode;

public class JayunMessageTest {
    private JayunMessage api = new JayunMessage(null);
    private HttpServletRequest request;

    @Test
    @Ignore
    /**
     * @param mobile 要注册的用户手机号
     */
    public void requestRegister(String mobile) {
        JayunMessage api = new JayunMessage(request);
        boolean result = api.requestRegister(mobile);
        if (result) {
            System.out.println("已向手机号发送验证码");
        } else {
            System.out.println(api.getMessage());
        }
    }

    @Test
    public void testSendSMSByMobileSendVoice() {
        api.setSendMode(SendMode.VOICE);
        boolean result = api.requestRegister("18566767108");
        System.out.println(api.getMessage());
        assertTrue("简讯发送失败", result);
    }

    @Test
    @Ignore
    /**
     * @param mobile     要注册的用户手机
     * @param verifyCode 用户收到的验证码
     */
    public void checkRegister(String mobile, String verifyCode) {
        JayunMessage api = new JayunMessage(request);
        boolean result = api.checkRegister(mobile, verifyCode);
        if (result) {
            System.out.println("验证通过");
        } else {
            System.out.println(api.getMessage());
        }
    }

    @Test
    public void testSendMessage() {
        boolean result = api.sendMessage("18100501", "wsd_transfer", "7108", "100", "RMB", "6580");
        System.out.println(api.getMessage());
        assertTrue(result);
    }
}
