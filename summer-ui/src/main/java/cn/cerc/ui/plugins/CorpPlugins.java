package cn.cerc.ui.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IPage;

/**
 * 用于公司别客制化需求
 * 
 * @author ZhangGong
 *
 */
public class CorpPlugins {
    private static final Logger log = LoggerFactory.getLogger(CorpPlugins.class);

    /**
     * 判断当前公司别当前对象，是否存在插件，如FrmProduct_131001（必须继承IPlugins）
     * 
     * @param owner 插件拥有者，一般为 form
     * 
     */
    public static boolean exists(Object owner, Class<? extends IPlugins> requiredType) {
        ApplicationContext context = Application.getContext();
        if (context == null)
            return false;
        String customName = getCustomClassName(owner);
        if (customName == null) {
            return false;
        }
        String[] beans = context.getBeanNamesForType(requiredType);
        for (String item : beans) {
            if (item.equals(customName))
                return true;
        }
        return false;
    }

    /**
     * 返回当前公司别当前对象之之插件对象，如FrmProduct_131001（必须继承IPlugins）
     * 
     * @param owner 插件拥有者，一般为 form
     */
    public static <T> T getBean(Object owner, Class<T> requiredType) {
        ApplicationContext context = Application.getContext();
        if (context == null)
            return null;
        String customName = getCustomClassName(owner);
        if (customName == null) {
            return null;
        }
        if (!context.containsBean(customName)) {
            return null;
        }
        T result = context.getBean(customName, requiredType);
        if (result != null) {
            // 要求必须继承IPlugins
            if (result instanceof IPlugins) {
                ((IPlugins) result).setOwner(owner);
            } else {
                log.warn("{} not supports IPlugins.", customName);
                return null;
            }
            if (result instanceof IHandle && owner instanceof IHandle) {
                ((IHandle) result).setSession(((IHandle) owner).getSession());
            }
        }
        return result;
    }

    protected static String getCustomClassName(Object owner) {
        String names[];
        if (owner instanceof Class)
            names = ((Class<?>) owner).getName().split("\\.");
        else
            names = owner.getClass().getName().split("\\.");
        String corpNo = null;
        if (owner instanceof IHandle)
            corpNo = ((IHandle) owner).getCorpNo();
        if (corpNo == null || "".equals(corpNo))
            return null;
        String target = names[names.length - 1] + "_" + corpNo;
        // 前两个字母都是大写，则不处理
        if (!target.substring(0, 2).toUpperCase().equals(target.substring(0, 2))) {
            target = target.substring(0, 1).toLowerCase() + target.substring(1, target.length());
        }
        return target;
    }

    /**
     * 取得调用者的函数名称
     * 
     * @return form的函数名称，如 execute append modify 等
     */
    protected final static String getSenderFuncCode() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];
        return e.getMethodName();
    }

    /**
     * 用于自定义 page 场景，或重定向到新的 form
     * 
     * @return 如返回 RedirectPage 对象
     */
    public final static IPage getRedirectPage(AbstractForm form) {
        IPlugins plugins = getBean(form, IPlugins.class);
        if (!(plugins instanceof IRedirectPage)) {
            return null;
        }
        return plugins != null ? ((IRedirectPage) plugins).getPage() : null;
    }

    /**
     * 用于自定义服务场影
     * 
     * @return 返回自定义 service 或 defaultService
     */
    public static String getService(AbstractForm form, String defaultService) {
        IPlugins plugins = getBean(form, IPlugins.class);
        if (plugins == null)
            return defaultService;
        if (!(plugins instanceof IServiceDefine)) {
            return defaultService;
        }
        String result = ((IServiceDefine) plugins).getService();
        return result != null ? result : defaultService;
    }

}
