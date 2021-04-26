package cn.cerc.mis.core;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ISession;
import cn.cerc.core.LanguageResource;
import cn.cerc.db.core.IAppConfig;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.SummerMIS;

@Component
public class Application implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final ClassConfig config = new ClassConfig(Application.class, SummerMIS.ID);
//    public static final String TOKEN = ISession.TOKEN;
//    public static final String bookNo = ISession.CORP_NO;
//    public static final String userCode = ISession.USER_CODE;
//    public static final String userName = ISession.USER_NAME;
    // tomcat JSESSION.ID
    public static final String SessionId = "sessionId";
    // FIXME 如下2个常量需要取消其引用，改为直接使用ISession
    @Deprecated
    public static final String UserId = "UserID";
    @Deprecated
    public static final String RoleCode = "RoleCode";
    // 签核代理用户列表，代理多个用户以半角逗号隔开
    public static final String ProxyUsers = "ProxyUsers";
    // 客户端代码
    public static final String ClientIP = "clientIP";
    // 本地会话登录时间
    public static final String LoginTime = "loginTime";
    // 浏览器通用客户设备Id
    public static final String WebClient = "webclient";
    // 图片静态路径
    private static String staticPath;
    // spring context
    private static ApplicationContext context;

    static {
        staticPath = config.getString("app.static.path", "");
    }

    /**
     * 根据 application.xml 初始化 spring context
     */
    public static void init() {
        initFromXml("application.xml");
    }

    /**
     * 根据参数 springXmlFile 初始化 spring context
     */
    public static void initFromXml(String springXmlFile) {
        if (context != null)
            return;
        setContext(new ClassPathXmlApplicationContext(springXmlFile));
    }

    /**
     * 根据 SummerConfiguration.class 初始化 spring context
     */
    public static void initOnlyFramework() {
        if (context != null)
            return;

        setContext(new AnnotationConfigApplicationContext(SummerConfiguration.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Application.setContext(applicationContext);
    }

    public static void setContext(ApplicationContext applicationContext) {
        if (context != applicationContext) {
            if (context != null) {
                log.error("applicationContext overload!");
            }
            context = applicationContext;
        }
    }

    public static ApplicationContext getContext() {
        return context;
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

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> requiredType) {
        String[] items = requiredType.getName().split("\\.");
        String itemId = items[items.length - 1];

        String beanId;
        if (itemId.substring(0, 2).toUpperCase().equals(itemId.substring(0, 2))) {
            beanId = itemId;
        } else {
            beanId = itemId.substring(0, 1).toLowerCase() + itemId.substring(1);
        }

        if (context.containsBean(beanId)) {
            return (T) context.getBean(beanId);
        } else {
            return context.getBean(requiredType);
        }
    }

    public static <T> T getBean(Class<T> requiredType, String... beans) {
        for (String key : beans) {
            if (context.containsBean(key)) {
                return context.getBean(key, requiredType);
            }
        }
        return null;
    }

    /**
     * 创建指定的实例，若实例支持 IHandle，就自动注入 session
     * 
     * 如创建ITokenManage，则自动查找 tokenManage, tokenManageDefault
     * 
     */
    public static <T> T getDefaultBean(IHandle handle, Class<T> requiredType) {
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
        if ((handle != null) && (result instanceof IHandle)) {
            ((IHandle) result).setSession(handle.getSession());
        }
        return result;
    }

    public static ISession createSession() {
        return getDefaultBean(null, ISession.class);
    }

    public static ISession createSession(HttpServletRequest request) {
        if (request != null) {
            Object object = request.getAttribute("summer_session");
            if (object != null)
                return (ISession) object;
        }
        ISession session = getDefaultBean(null, ISession.class);
        if (session != null)
            request.setAttribute("summer_session", session);
        return session;
    }

    public static IService getService(IHandle handle, String serviceCode) {
        IService bean = context.getBean(serviceCode, IService.class);
        if (bean != null && handle != null) {
            bean.setHandle(handle);
        }
        return bean;
    }

    public static IPassport getPassport(IHandle handle) {
        return getDefaultBean(handle, IPassport.class);
    }

    public static ISystemTable getSystemTable() {
        return getDefaultBean(null, ISystemTable.class);
    }

    public static String getLanguageId() {
        String lang = ServerConfig.getInstance().getProperty(ISession.LANGUAGE_ID);
        if (lang == null || "".equals(lang) || LanguageResource.appLanguage.equals(lang)) {
            return LanguageResource.appLanguage;
        } else if (LanguageResource.LANGUAGE_EN.equals(lang)) {
            return lang;
        } else {
            throw new RuntimeException("not support language: " + lang);
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

    public static ITokenManage getTokenManage(IHandle handle) {
        return getDefaultBean(handle, ITokenManage.class);
    }

    public static IAppConfig getConfig() {
        return getBean(IAppConfig.class);
    }

}
