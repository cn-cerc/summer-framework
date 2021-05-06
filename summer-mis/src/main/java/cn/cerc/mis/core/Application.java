package cn.cerc.mis.core;

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
    public static ApplicationContext init() {
        initFromXml("application.xml");
        return context;
    }

    /**
     * 根据参数 springXmlFile 初始化 spring context
     */
    public static ApplicationContext initFromXml(String springXmlFile) {
        if (context == null)
            setContext(new ClassPathXmlApplicationContext(springXmlFile));
        return context;
    }

    /**
     * 根据 SummerConfiguration.class 初始化 spring context
     */
    public static ApplicationContext initOnlyFramework() {
        if (context == null) {
            // FIXME: 自定义作用域，临时解决 request, session 问题
            RequestScope scope = new RequestScope();

            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                    SummerSpringConfiguration.class);
            context.getBeanFactory().registerScope(RequestScope.REQUEST_SCOPE, scope);
            context.getBeanFactory().registerScope(RequestScope.SESSION_SCOPE, scope);

            setContext(context);
        }
        return context;
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

    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    public static Object getBean(String beanId) {
        return context.getBean(beanId);
    }

    public static ISession getSession() {
        return context.getBean(ISession.class);
    }

    public static IAppConfig getConfig() {
        return context.getBean(IAppConfig.class);
    }

    public static ISystemTable getSystemTable() {
        return context.getBean(ISystemTable.class);
    }

    public static <T> T getBean(IHandle handle, Class<T> requiredType) {
        T bean = context.getBean(requiredType);
        if ((handle != null) && (bean instanceof IHandle))
            ((IHandle) bean).setSession(handle.getSession());
        return bean;
    }

    public static Object getBean(IHandle handle, String beanId) {
        Object bean = context.getBean(beanId);
        if ((handle != null) && (bean instanceof IHandle))
            ((IHandle) bean).setSession(handle.getSession());
        return bean;
    }

    public static Object getBean(ISession session, String beanId) {
        Object bean = context.getBean(beanId);
        if ((session != null) && (bean instanceof IHandle))
            ((IHandle) bean).setSession(session);
        return bean;
    }

    public static IPassport getPassport(IHandle handle) {
        return getBean(handle, IPassport.class);
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

    public static String getStaticPath() {
        return staticPath;
    }

}
