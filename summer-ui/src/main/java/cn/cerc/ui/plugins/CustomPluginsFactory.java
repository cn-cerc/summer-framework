package cn.cerc.ui.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import cn.cerc.db.core.ISessionOwner;
import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.Application;

public abstract class CustomPluginsFactory {
    private static final Logger log = LoggerFactory.getLogger(CustomPluginsFactory.class);

    /**
     * 判断当前公司别当前对象，是否存在插件，如FrmProduct_131001（必须继承IPlugins）
     * 
     * @param owner
     * @return 返回true or false
     */
    protected static boolean exists(Object owner) {
        ApplicationContext context = Application.getContext();
        if (context == null)
            return false;
        String names[] = owner.getClass().getName().split("\\.");
        String corpNo = null;
        if (owner instanceof ISessionOwner)
            corpNo = ((ISessionOwner) owner).getCorpNo();
        if (corpNo == null || "".equals(corpNo))
            return false;
        String target = names[names.length - 1] + "_" + corpNo;
        target = target.substring(0, 1).toLowerCase() + target.substring(1, target.length());
        String[] beans = context.getBeanNamesForType(IPlugins.class);
        for (String item : beans) {
            if (item.equals(target))
                return true;
        }
        return false;
    }

    /**
     * 返回当前公司别当前对象之之插件对象，如FrmProduct_131001（必须继承IPlugins）
     * 
     * @param owner
     * @return 返回插件对象，或返回null
     */
    protected static <T> T get(Object owner, Class<T> requiredType) {
        ApplicationContext context = Application.getContext();
        if (context == null)
            return null;
        String names[] = owner.getClass().getName().split("\\.");
        String corpNo = null;
        if (owner instanceof ISessionOwner)
            corpNo = ((ISessionOwner) owner).getCorpNo();
        if (corpNo == null || "".equals(corpNo))
            return null;
        String target = names[names.length - 1] + "_" + corpNo;
        target = target.substring(0, 1).toLowerCase() + target.substring(1, target.length());
        T result = context.getBean(target, requiredType);
        if (result != null) {
            // 要求必须继承IPlugins
            if (result instanceof IPlugins) {
                ((IPlugins) result).setOwner(owner);
            } else {
                log.warn("{} not supports IPlugins.", target);
                return null;
            }
            if (result instanceof ISessionOwner) {
                ((ISessionOwner) result).setSession(((ISessionOwner) owner).getSession());
            }
        }
        return result;
    }

    public final static IRedirectPage getRedirectPage(AbstractForm owner) {
        return get(owner, IRedirectPage.class);
    }

    public final static IContextDefine getContextDefine(AbstractForm owner) {
        return get(owner, IContextDefine.class);
    }

}
