package cn.cerc.ui.custom;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ISession;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.AppClient;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IAppLogin;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.IUserLoginCheck;
import cn.cerc.ui.page.JspPage;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppLoginDefault extends JspPage implements IAppLogin {
    private ISession session;
    private static final Logger log = LoggerFactory.getLogger(AppLoginDefault.class);
    // 注意：此处应该使用SummerMVC.ID，别改为SummerUI.ID
    private static final ClassConfig config = new ClassConfig(AppLoginDefault.class, SummerMIS.ID);

    // 配置在服务器的用户名下面 summer-application.properties
    @Deprecated
    public static final String Notify_Url = "app.notify_url";

    public AppLoginDefault() {
        super();
    }

    @Override
    public void init(IForm form) {
        this.setForm(form);
        this.setJspFile(config.getString(Application.JSPFILE_LOGIN, "common/FrmLogin.jsp"));
        this.setSession(form.getHandle().getSession());
        this.add("homePage", config.getString(Application.FORM_WELCOME, "welcome"));
        this.add("needVerify", "false");
        String logoUrl = config.getString("vine.mall.logoUrl", "");
        if (!"".equals(logoUrl)) {
            this.add("logoUrl", logoUrl);
        }
        String supCorpNo = config.getString("vine.mall.supCorpNo", "");
        if (!"".equals(supCorpNo)) {
            this.add("supCorpNo", supCorpNo);
        }
    }

    @Override
    public String checkToken(String token) throws IOException, ServletException {
        try {
            log.debug("create session by token {}", token);
            ITokenManage manage = Application.getBeanDefault(ITokenManage.class,
                    this.getForm().getHandle().getSession());
            if (manage.resumeToken(token)) {
                return null;
            }
        } catch (Exception e) {
            this.add("loginMsg", e.getMessage());
        }
        // 返回指定的jsp页面
        return this.execute();
    }

    @Override
    public String checkLogin(String userCode, String password) throws ServletException, IOException {
        IForm form = this.getForm();
        HttpServletRequest req = this.getRequest();

        log.debug(String.format("校验用户帐号(%s)与密码", userCode));

        // 进行设备首次登记
        String deviceId = form.getClient().getId();
        req.setAttribute("userCode", userCode);
        req.setAttribute("password", password);
        req.setAttribute("needVerify", "false");

        IUserLoginCheck obj = Application.getBeanDefault(IUserLoginCheck.class, form.getHandle().getSession());

        // 如长度大于10表示用手机号码登入
        if (userCode.length() > 10) {
            String oldCode = userCode;
            userCode = obj.getUserCode(oldCode);
            log.debug(String.format("将手机号 %s 转化成帐号 %s", oldCode, userCode));
        }

        log.debug(String.format("进行用户帐号(%s)与密码认证", userCode));
        // 进行用户名、密码认证
        String IP = AppClient.getIP(this.getRequest());
        if (obj.check(userCode, password, deviceId, IP, form.getClient().getLanguage())) {
            String token = obj.getToken();
            if (token != null && !"".equals(token)) {
                log.debug(String.format("认证成功，取得sid(%s)", token));
                ((AppClient) this.getForm().getClient()).setToken(token);
            }
            req.getSession().setAttribute("loginMsg", "");
            req.getSession().setAttribute("mobile", "");
        } else {
            // 登录验证失败
            log.debug(String.format("用户帐号(%s)与密码认证失败", userCode));
            req.getSession().setAttribute("loginMsg", obj.getMessage());
            return this.execute();
        }
        return null;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

}
