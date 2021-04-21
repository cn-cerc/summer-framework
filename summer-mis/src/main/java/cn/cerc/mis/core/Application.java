package cn.cerc.mis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ISession;
import cn.cerc.core.LanguageResource;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ISessionOwner;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.mis.SummerMIS;

@Component
public class Application implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final ClassConfig config = new ClassConfig(Application.class, SummerMIS.ID);
    // tomcat JSESSION.ID
    public static final String sessionId = "sessionId";
    // FIXME 如下5个常量需要取消其引用，改为直接使用ISession
    public static final String TOKEN = ISession.TOKEN;
    public static final String bookNo = ISession.CORP_NO;
    public static final String userCode = ISession.USER_CODE;
    public static final String userName = ISession.USER_NAME;
    @Deprecated
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
        setContext(new ClassPathXmlApplicationContext(xmlFile));
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

    public static <T> T getBean(Class<T> requiredType) {
        String items[] = context.getBeanNamesForType(requiredType);
        if (items.length > 1) {
            log.warn("{} size {} > 1", requiredType.getName(), items.length);
        }
        for (String beanId : items) {
            return context.getBean(beanId, requiredType);
        }
        return null;
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

    public static ISession createSession() {
        return getBeanDefault(ISession.class, null);
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

    public static String getLanguage() {
        String lang = ServerConfig.getInstance().getProperty(ISession.LANGUAGE_ID);
        if (lang == null || "".equals(lang) || App_Language.equals(lang)) {
            return App_Language;
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

    public static ITokenManage getTokenManage(ISession session) {
        return getBeanDefault(ITokenManage.class, session);
    }

    public static String getHomePage() {
        return config.getString(Application.FORM_DEFAULT, "default");
    }

    private static String getAppLanguage() {
        return LanguageResource.appLanguage;
    }

}
