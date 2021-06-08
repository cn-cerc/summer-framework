package cn.cerc.ui.custom;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Utils;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IAppLogin;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.IUserLoginCheck;
import cn.cerc.ui.page.JspPage;

@Component
public class AppLoginDefault implements IAppLogin {
    // 注意：此处应该使用SummerMVC.ID，别改为SummerUI.ID
    private static final ClassConfig config = new ClassConfig(AppLoginDefault.class, SummerMIS.ID);
    private static final Logger log = LoggerFactory.getLogger(AppLoginDefault.class);

    @Override
    public String getLoginView(IForm form) throws IOException, ServletException {
        JspPage jspPage = new JspPage();
        jspPage.setForm(form);
        jspPage.setJspFile(Application.getConfig().getJspLoginFile());
        jspPage.add("homePage", Application.getConfig().getWelcomePage());
        jspPage.add("needVerify", "false");
        String logoUrl = config.getString("vine.mall.logoUrl", "");
        if (!"".equals(logoUrl))
            jspPage.add("logoUrl", logoUrl);

        HttpServletRequest request = form.getRequest();

        // 若页面有传递用户帐号，则强制重新校验
        String userCode = request.getParameter("login_usr");
        String password = request.getParameter("login_pwd");
        if (userCode != null && password != null) {
            log.debug(String.format("校验用户帐号(%s)与密码", userCode));
            request.setAttribute("userCode", userCode);
            request.setAttribute("password", password);
            request.setAttribute("needVerify", "false");

            IUserLoginCheck loginCheck = Application.getBean(form, IUserLoginCheck.class);

            // 如长度大于10表示用手机号码登入
            if (userCode.length() > 10) {
                String oldCode = userCode;
                userCode = loginCheck.getUserCode(oldCode);
                log.debug(String.format("将手机号 %s 转化成帐号 %s", oldCode, userCode));
            }

            // 进行用户名、密码认证
            String deviceId = form.getClient().getId();
            String clientIP = AppClient.getClientIP(request);
            String token = loginCheck.createToken(userCode, password, deviceId, clientIP,
                    form.getClient().getLanguage());
            if (!Utils.isEmpty(token)) {
                log.debug(String.format("认证成功，取得sid(%s)", token));
                ((AppClient) form.getClient()).setToken(token);
                request.getSession().setAttribute("loginMsg", "");
                request.getSession().setAttribute("mobile", "");
                return null;
            } else {
                // 登录验证失败
                log.debug(String.format("用户帐号(%s)与密码认证失败", userCode));
                request.getSession().setAttribute("loginMsg", loginCheck.getMessage());
                return jspPage.execute();
            }
        }

        // 返回登录页面
        return jspPage.execute();
    }

}
