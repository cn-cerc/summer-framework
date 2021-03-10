package cn.cerc.mis.core;

import cn.cerc.core.ClassConfig;
import cn.cerc.mvc.SummerMVC;

@Deprecated
public enum HTMLResource {
    
    INSTANCE;
    
    private static final ClassConfig config = new ClassConfig(HTMLResource.class, SummerMVC.ID);
    
    public static String getVersion() {
        return config.getString("browser.cache.version", "1.0.0.0");
    }

}
