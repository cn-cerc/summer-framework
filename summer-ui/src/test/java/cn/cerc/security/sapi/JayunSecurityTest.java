package cn.cerc.security.sapi;

import javax.servlet.http.HttpServletRequest;

import org.junit.Ignore;
import org.junit.Test;

import security.sapi.JayunSecurity;

public class JayunSecurityTest {
    private JayunSecurity api = new JayunSecurity(null);
    private HttpServletRequest request;

    @Test
    @Ignore
    /**
     * @param user   应用用户帐号
     * @param mobile 应用用户手机
     */
    public void register(String user, String mobile) {
        JayunSecurity api = new JayunSecurity(request);
        boolean result = api.register(user, mobile);
        if (result) {
            System.out.println("关联成功");
        } else {
            System.out.println(api.getMessage());
        }
    }

    @Test
    @Ignore
    /**
     * @param user 应用用户帐号
     */
    public void isSecurity(String user) {
        boolean result = api.isSecurity(user);
        if (result) {
            System.out.println("环境安全");
        } else {
            System.out.println(api.getMessage());
        }
    }

    @Test
    @Ignore
    /**
     * @param user 应用用户帐号
     */
    public void requestVerify(String user) {
        JayunSecurity api = new JayunSecurity(request);
        boolean result = api.requestVerify(user);
        if (result) {
            System.out.println("已向用户手机发送验证码");
        } else {
            System.out.println(api.getMessage());
        }
    }

    @Test
    @Ignore
    /**
     * @param user       应用用户帐号
     * @param verifyCode 用户验证码
     */
    public void checkVerify(String user, String verifyCode) {
        JayunSecurity api = new JayunSecurity(request);
        boolean result = api.checkVerify(user, verifyCode);
        if (result) {
            System.out.println("验证通过");
        } else {
            System.out.println(api.getMessage());
        }
    }

    /**
     * @param user 应用用户帐号
     */
    public void checkEnvironment(String user) {
        JayunSecurity sapi = new JayunSecurity(request);
        if (sapi.checkEnvironment(user)) {
            System.out.println("当前环境安全");
        } else {
            System.out.println("当前环境不安全，请发送并检查验证码");
        }
    }
}
