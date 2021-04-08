package cn.cerc.mis.core;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.core.LanguageResource;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ISessionOwner;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.SummerMIS;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final ClassResource res = new ClassResource(Application.class, SummerMIS.ID);
    private static final ClassConfig config = new ClassConfig(Application.class, SummerMIS.ID);
    // tomcat JSESSION.ID
    public static final String sessionId = "sessionId";
    // FIXME 如下5个常量需要取消其引用，改为直接使用ISession
    public static final String TOKEN = ISession.TOKEN;
    public static final String bookNo = ISession.CORP_NO;
    public static final String userCode = ISession.USER_CODE;
    public static final String userName = ISession.USER_NAME;
    public static final String deviceLanguage = ISession.LANGUAGE_ID;
    @Deprecated
    public static final String userId = "UserID";
    @Deprecated
    public static final String roleCode = "RoleCode";
    // 签核代理用户列表，代理多个用户以半角逗号隔开
    public static final String ProxyUsers = "ProxyUsers";
    // 客户端代码
    public static final String clientIP = "clientIP";
    // 本地会话登录时间
    public static final String loginTime = "loginTime";
    // 浏览器通用客户设备Id
    public static final String webclient = "webclient";

    // 默认界面语言版本
    // FIXME: 2019/12/7 此处应改为从函数，并从配置文件中读取默认的语言类型
    public static final String App_Language = getAppLanguage(); // 可选：cn/en

    // 各类应用配置代码
    public static final String PATH_FORMS = "application.pathForms";
    public static final String PATH_SERVICES = "application.pathServices";
    public static final String FORM_WELCOME = "application.formWelcome";
    public static final String FORM_DEFAULT = "application.formDefault";
    public static final String FORM_LOGOUT = "application.formLogout";
    public static final String FORM_VERIFY_DEVICE = "application.formVerifyDevice";
    public static final String JSPFILE_LOGIN = "application.jspLoginFile";
    public static final String FORM_ID = "formId";

    private static ApplicationContext context;
    private static String staticPath;

    static {
        staticPath = config.getString("app.static.path", "");
    }

    public static void init(String packageId) {
        if (context != null)
            return;
        String xmlFile = String.format("%s-spring.xml", packageId);
        if (packageId == null)
            xmlFile = "application.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(xmlFile);
        context = ctx;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    private static String getAppLanguage() {
        return LanguageResource.appLanguage;
    }

    public static void setContext(ApplicationContext applicationContext) {
        if (context != applicationContext) {
            if (context != null) {
                log.warn("applicationContext overload!");
            }
            context = applicationContext;
        }
    }

    public static ApplicationContext get(ServletContext servletContext) {
        setContext(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
        return context;
    }

    public static ApplicationContext get(ServletRequest request) {
        setContext(WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()));
        return context;
    }

    // 请改为使用下面的函数
    @Deprecated
    public static <T> T getBean(String beanId, Class<T> requiredType) {
        return context.getBean(beanId, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType, String... beans) {
        for (String key : beans) {
            if (!context.containsBean(key)) {
                continue;
            }
            return context.getBean(key, requiredType);
        }
        return null;
    }

    /**
     * 创建指定的实例，若实例支持 ISessionOwner，就自动注入 session
     * 
     * 如创建ITokenManage，则自动查找 tokenManage, tokenManageDefault
     * 
     * @param <T>
     * @param requiredType
     * @param session
     * @return
     */
    public static <T> T getBeanDefault(Class<T> requiredType, ISession session) {
        String[] items = requiredType.getName().split("\\.");
        String itemId = items[items.length - 1];
        String classId = itemId;
        // 遇到接口(前2个字母均是大写)，去掉第一个字母
        if (itemId.substring(0, 2).toUpperCase().equals(itemId.substring(0, 2))) {
            classId = itemId.substring(1);
        }
        String beanId = classId.substring(0, 1).toLowerCase() + classId.substring(1);
        // 找不到自定义的，就再查找默认的类
        T result = getBean(requiredType, beanId, beanId + "Default");
        // 自动注入 session
        if ((session != null) && (result instanceof ISessionOwner)) {
            ((ISessionOwner) result).setSession(session);
        }
        return result;
    }

    public static <T> T getBean(Class<T> requiredType) {
        String[] items = requiredType.getName().split("\\.");
        String itemId = items[items.length - 1];

        String beanId;
        if (itemId.substring(0, 2).toUpperCase().equals(itemId.substring(0, 2))) {
            beanId = itemId;
        } else {
            beanId = itemId.substring(0, 1).toLowerCase() + itemId.substring(1);
        }

        return context.getBean(beanId, requiredType);
    }

    @Deprecated
    public static IHandle getHandle() {
        return new Handle(createSession());
    }

    public static ISession createSession() {
        return getBeanDefault(ISession.class, null);
    }

    /**
     * 请改为直接使用ClassResource，参考AppConfigDefault
     *
     * @return
     */
    @Deprecated
    public static IAppConfig getAppConfig() {
        return getBeanDefault(IAppConfig.class, null);
    }

    @Deprecated // 请改使用 getBean
    public static <T> T get(IHandle handle, Class<T> requiredType) {
        return getBean(handle, requiredType);
    }

    public static <T> T getBean(IHandle handle, Class<T> requiredType) {
        T bean = getBean(requiredType);
        if (bean != null && handle != null) {
            if (bean instanceof IHandle) {
                ((IHandle) bean).setSession(handle.getSession());
            }
        }
        return bean;
    }

    public static IService getService(IHandle handle, String serviceCode) {
        IService bean = context.getBean(serviceCode, IService.class);
        if (bean != null && handle != null) {
            bean.setHandle(handle);
        }
        return bean;
    }

    public static IPassport getPassport(ISession session) {
        return getBeanDefault(IPassport.class, session);
    }

    public static IPassport getPassport(ISessionOwner owner) {
        return getBeanDefault(IPassport.class, owner.getSession());
    }

    public static ISystemTable getSystemTable() {
        return getBeanDefault(ISystemTable.class, null);
    }

    public static IForm getForm(HttpServletRequest req, HttpServletResponse resp, String formId) {
        if (formId == null || "".equals(formId) || "service".equals(formId)) {
            return null;
        }

        setContext(WebApplicationContextUtils.getRequiredWebApplicationContext(req.getServletContext()));

        if (!context.containsBean(formId)) {
            throw new RuntimeException(String.format("form %s not find!", formId));
        }

        IForm form = context.getBean(formId, IForm.class);
        if (form != null) {
            form.setRequest(req);
            form.setResponse(resp);
        }

        return form;
    }

    public static String getLanguage() {
        String lang = ServerConfig.getInstance().getProperty(deviceLanguage);
        if (lang == null || "".equals(lang) || App_Language.equals(lang)) {
            return App_Language;
        } else if (LanguageResource.LANGUAGE_EN.equals(lang)) {
            return lang;
        } else {
            throw new RuntimeException("not support language: " + lang);
        }
    }

    public static String getFormView(HttpServletRequest req, HttpServletResponse resp, String formId, String funcCode,
            String... pathVariables) {
        // 设置登录开关
        req.setAttribute("logon", false);

        // 验证菜单是否启停
        IFormFilter formFilter = Application.getBean(IFormFilter.class, "AppFormFilter");
        if (formFilter != null) {
            try {
                if (formFilter.doFilter(resp, formId, funcCode)) {
                    return null;
                }
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }

        ISession session = null;
        try {
            IForm form = Application.getForm(req, resp, formId);
            if (form == null) {
                outputErrorPage(req, resp, new RuntimeException("error servlet:" + req.getServletPath()));
                return null;
            }

            // 设备讯息
            AppClient client = new AppClient();
            client.setRequest(req);
            req.setAttribute("_showMenu_", !AppClient.ee.equals(client.getDevice()));
            form.setClient(client);

            // 建立数据库资源
            session = Application.createSession();
            ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
            manage.resumeToken((String) req.getSession().getAttribute(RequestData.TOKEN));
            session.setProperty(Application.sessionId, req.getSession().getId());
            session.setProperty(Application.deviceLanguage, client.getLanguage());
            session.setProperty(Application.TOKEN, req.getSession().getAttribute(RequestData.TOKEN));
            session.setProperty(ISession.REQUEST, req);
            IHandle handle = new Handle(session);
            req.setAttribute("myappHandle", handle);
            form.setId(formId);
            form.setHandle(handle);

            // 传递路径变量
            form.setPathVariables(pathVariables);

            // 当前Form需要安全检查
            if (form.allowGuestUser()) {
                return form.getView(funcCode);
            }

            // 用户已登录系统
            if (session.logon()) {
                // 权限检查
                if (!Application.getPassport(session).pass(form)) {
                    resp.setContentType("text/html;charset=UTF-8");
                    JsonPage output = new JsonPage(form);
                    output.setResultMessage(false, res.getString(1, "对不起，您没有权限执行此功能！"));
                    output.execute();
                    return null;
                }
            } else {
                // 登录验证
                IAppLogin appLogin = Application.getBeanDefault(IAppLogin.class, session);
                if (!appLogin.pass(form)) {
                    return appLogin.getJspFile();
                }
            }

            // 设备校验
            if (form.isSecurityDevice()) {
                return form.getView(funcCode);
            }

            ISecurityDeviceCheck deviceCheck = Application.getBeanDefault(ISecurityDeviceCheck.class, session);
            switch (deviceCheck.pass(form)) {
            case PASS:
                return form.getView(funcCode);
            case CHECK:
                return "redirect:" + config.getString(Application.FORM_VERIFY_DEVICE, "VerifyDevice");
            default:
                resp.setContentType("text/html;charset=UTF-8");
                JsonPage output = new JsonPage(form);
                output.setResultMessage(false, res.getString(2, "对不起，当前设备被禁止使用！"));
                output.execute();
                return null;
            }
        } catch (Exception e) {
            outputErrorPage(req, resp, e);
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static void outputView(HttpServletRequest request, HttpServletResponse response, String url)
            throws IOException, ServletException {
        if (url == null)
            return;

        if (url.startsWith("redirect:")) {
            String redirect = url.substring(9);
            redirect = response.encodeRedirectURL(redirect);
            response.sendRedirect(redirect);
            return;
        }

        // 输出jsp文件
        String jspFile = String.format("/WEB-INF/%s/%s", config.getString(Application.PATH_FORMS, "forms"), url);
        request.getServletContext().getRequestDispatcher(jspFile).forward(request, response);
    }

    public static void outputErrorPage(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        Throwable err = e.getCause();
        if (err == null) {
            err = e;
        }
        IAppErrorPage errorPage = Application.getBeanDefault(IAppErrorPage.class, null);
        if (errorPage != null) {
            String result = errorPage.getErrorPage(request, response, err);
            if (result != null) {
                String url = String.format("/WEB-INF/%s/%s", config.getString(Application.PATH_FORMS, "forms"), result);
                try {
                    request.getServletContext().getRequestDispatcher(url).forward(request, response);
                } catch (ServletException | IOException e1) {
                    log.error(e1.getMessage());
                    e1.printStackTrace();
                }
            }
        } else {
            log.warn("not define bean: errorPage");
            log.error(err.getMessage());
            err.printStackTrace();
        }
    }

    /**
     * 获得 token 的值
     * 
     * @param handle
     * @return
     */
    public static String getToken(IHandle handle) {
        return handle.getSession().getToken();
    }

    public static String getStaticPath() {
        return staticPath;
    }

    public static ITokenManage getTokenManage(ISession session) {
        return getBeanDefault(ITokenManage.class, session);
    }

    public static String getHomePage() {
        return config.getString(Application.FORM_DEFAULT, "default");
    }

}
