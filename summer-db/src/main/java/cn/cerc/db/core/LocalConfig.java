package cn.cerc.db.core;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.IConfig;

/**
 * 请改使用 ClassConfig
 * 
 * @author ZhangGong
 *
 */
@Deprecated
public enum LocalConfig implements IConfig {

    INSTANCE;
    
    @Deprecated
    public static final String path = System.getProperty("user.home") + System.getProperty("file.separator");

    private static final ClassConfig properties = new ClassConfig();

    public static LocalConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public String getProperty(String key, String def) {
        return properties.getProperty(key, def);
    }

}
