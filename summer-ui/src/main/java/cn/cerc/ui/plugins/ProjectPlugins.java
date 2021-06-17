package cn.cerc.ui.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import cn.cerc.core.ClassConfig;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.Application;

/**
 * 用于项目级客制化需求
 * 
 * @author 张弓
 *
 */
public class ProjectPlugins {
    private static final Logger log = LoggerFactory.getLogger(ProjectPlugins.class);
    private static final ClassConfig config = new ClassConfig(ProjectPlugins.class, null);
    private static final String ProjectId = "application.id";
    private static final String projectId;

    static {
        projectId = config.getString(ProjectId, "").trim();
    }

    /**
     * 判断当前项目的当前对象，是否存在插件，如FrmProduct_Diaoyou（必须继承IPlugins）
     * 
     * @param owner 插件拥有者，一般为 form
     * 
     */
    public static boolean exists(Object owner, Class<? extends IPlugins> requiredType) {
        if (projectId == null || "".equals(projectId))
            return false;
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
     * 返回当前项目的当前对象之插件对象，如FrmProduct_Diaoyou（必须继承IPlugins）
     * 
     * @param owner 插件拥有者，一般为 form
     */
    public static <T> T getBean(Object owner, Class<T> requiredType) {
        if (projectId == null || "".equals(projectId))
            return null;
        ApplicationContext context = Application.getContext();
        if (context == null)
            return null;
        String customName = getCustomClassName(owner);
        if (customName == null) {
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
        if (projectId == null || "".equals(projectId))
            return null;
        String target = names[names.length - 1] + "_" + projectId;
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

}
