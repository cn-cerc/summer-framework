package cn.cerc.mis.page;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import cn.cerc.core.IHandle;
import cn.cerc.core.SupportHandle;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.AbstractJspPage;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ClientDevice;
import cn.cerc.mis.core.IAppLogin;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.IUserLoginCheck;
import cn.cerc.mis.core.RequestData;
import cn.cerc.mis.page.qrcode.SocketTool;
import cn.cerc.security.sapi.JayunAPI;
import cn.cerc.security.sapi.JayunSecurity;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AppLoginDefault extends AbstractJspPage implements IAppLogin {

    private static final Logger log = LoggerFactory.getLogger(AppLoginDefault.class);

    // 配置在服务器的用户名下面 summer-application.properties
    public static final String Notify_Url = "app.notify_url";

    public AppLoginDefault() {
        super();
    }

    @Override
    public void init(IForm form) {
        this.setForm(form);
        IAppConfig conf = Application.getAppConfig();
        this.setJspFile(conf.getJspLoginFile());
        this.add("homePage", conf.getFormWelcome());
        this.add("needVerify", "false");
        ServerConfig config = ServerConfig.getInstance();
        String logoUrl = config.getProperty("vine.mall.logoUrl", "");
        if (!"".equals(logoUrl)) {
            this.add("logoUrl", logoUrl);
        }
        String supCorpNo = config.getProperty("vine.mall.supCorpNo", "");
        if (!"".equals(supCorpNo)) {
            this.add("supCorpNo", supCorpNo);
        }

        if (form.getClient().isPhone()) {
            return;
        }

        // 获取域名
        SocketTool tool = new SocketTool();
        String domain = tool.getDomain(getRequest());

        String socket_url = tool.getSocketUrl(getRequest());
        this.add("socketUrl", socket_url);

        // 判断当前客户端类型
        log.info("current client device type: {}", form.getClient().getDevice());
        boolean isWeb = RequestData.webclient.equals(form.getClient().getId());
        this.add("isWeb", isWeb);
        if (!isWeb) {
            return;
        }

        String appKey = config.getProperty(JayunAPI.JAYUN_APP_KEY);
        Map<String, Object> items = new TreeMap<>();
        items.put("appKey", appKey);
        items.put("action", "login");
        items.put("sessionId", getRequest().getSession().getId());
        items.put("domain", domain);

        String notify_url = config.getProperty(Notify_Url);
        if (notify_url != null && !"".equals(notify_url)) {
            items.put("notify_url", notify_url);
            log.warn("notify_url {}", notify_url);
        }

        JayunSecurity api = new JayunSecurity(form.getRequest());
        boolean result = api.encodeQrcode(new Gson().toJson(items));
        if (!result) {
            log.error(api.getMessage());
            this.add("msg", api.getMessage());
        }
        this.add("qrcode", (String) api.getData());
    }

    @Override
    public String checkToken(String token) throws IOException, ServletException {
        IForm form = this.getForm();
        String password = null;
        String userCode = null;
        try {
            // TODO 需要统一 login_user login_pwd 与 userCode password 的名称
            if (form.getRequest().getParameter("login_usr") != null) {
                userCode = getRequest().getParameter("login_usr");
                password = getRequest().getParameter("login_pwd");
                return checkLogin(userCode, password);
            }

            log.debug(String.format("根据 token(%s) 创建 Session", token));

            IHandle sess = (IHandle) form.getHandle().getProperty(null);
            if (sess.init(token)) {
                return null;
            }
            if (form.logon()) {
                return null;
            }
        } catch (Exception e) {
            this.add("loginMsg", e.getMessage());
        }
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

        IUserLoginCheck obj = Application.getBean("userLoginCheck", IUserLoginCheck.class);
        if (obj != null ) {
            if (obj instanceof SupportHandle) {
                if (form instanceof AbstractForm)
                    ((SupportHandle) obj).init((AbstractForm) form);
                else
                    ((SupportHandle) obj).init(form.getHandle());
            }
        }

        // 如长度大于10表示用手机号码登入
        if (userCode.length() > 10) {
            String oldCode = userCode;
            userCode = obj.getTelToUserCode(oldCode);
            log.debug(String.format("将手机号 %s 转化成帐号 %s", oldCode, userCode));
        }

        log.debug(String.format("进行用户帐号(%s)与密码认证", userCode));
        // 进行用户名、密码认证
        String IP = getIPAddress();
        if (obj.check(userCode, password, deviceId, IP, form.getClient().getLanguage())) {
            String sid = obj.getSessionId();
            if (sid != null && !sid.equals("")) {
                log.debug(String.format("认证成功，取得sid(%s)", sid));
                ((ClientDevice) this.getForm().getClient()).setSid(sid);
            }
            // 登记聚安应用帐号
            String mobile = Utils.safeString(obj.getMobile());
            if (mobile != null && !"".equals(mobile)) {
                JayunSecurity api = new JayunSecurity(req);
                if (!api.register(userCode, mobile)) {
                    log.error(api.getMessage());
                }
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

    /**
     * @return 获取客户端IP地址
     */
    public String getIPAddress() {
        String ip = this.getRequest().getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = this.getRequest().getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = this.getRequest().getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = this.getRequest().getRemoteAddr();
        }
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "0.0.0.0";
        }
        return ip;
    }
}
