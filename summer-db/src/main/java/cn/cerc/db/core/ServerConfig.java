package cn.cerc.db.core;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.IConfig;
import cn.cerc.db.SummerDB;

public enum ServerConfig implements IConfig {

    INSTANCE;

    private static final ClassConfig config = new ClassConfig(ServerConfig.class, SummerDB.ID);

    public static ServerConfig getInstance() {
        return INSTANCE;
    }

    // 是否为debug状态
    private int debug = -1;

    public static boolean enableTaskService() {
        return config.getBoolean("task.service", false);
    }

    public static String getAppName() {
        return config.getString("appName", "localhost");
    }

    public static boolean enableDocService() {
        return config.getBoolean("docs.service", false);
    }

    // 正式环境
    public static boolean isServerMaster() {
        String tmp = config.getString("version", "develop"); 
        if ("release".equals(tmp)) {
            return true;
        }
        return "master".equals(tmp);
    }

    // 测试环境
    public static boolean isServerBeta() {
        String tmp = config.getString("version", "develop");
        return "beta".equals(tmp);
    }

    // 开发环境
    public static boolean isServerDevelop() {
        if (isServerMaster()) {
            return false;
        }
        if (isServerBeta()) {
            return false;
        }
        return true;
    }

    /**
     * 读取配置，请改为使用 ClassConfig
     */
    @Deprecated
    @Override
    public String getProperty(String key, String def) {
        return config.getString(key, def);
    }

    /**
     * @return 返回当前是否为debug状态
     */
    public boolean isDebug() {
        if (debug == -1) {
            debug = config.getBoolean("debug", false) ? 1 : 0;
        }
        return debug == 1;
    }
}
