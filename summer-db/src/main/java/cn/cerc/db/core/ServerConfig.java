package cn.cerc.db.core;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.IConfig;

public enum ServerConfig implements IConfig {

    INSTANCE;

    // 是否为任务主机
    public static final String TaskServiceEnabled = "task.service";
    public static final String config_version = "version";
    public static final String config_debug = "debug";
    public static final String CONFIG_APP_NAME = "appName";
    private static final ClassConfig config = new ClassConfig();

    public static ServerConfig getInstance() {
        return INSTANCE;
    }

    // 是否为debug状态
    private int debug = -1;

    public static boolean enableTaskService() {
        return "1".equals(config.getProperty(TaskServiceEnabled, null));
    }

    public static String getAppName() {
        return config.getProperty(CONFIG_APP_NAME, "localhost");
    }

    public static boolean enableDocService() {
        return "1".equals(config.getProperty("docs.service", "0"));
    }

    // 正式环境
    public static boolean isServerMaster() {
        String tmp = config.getProperty("version", "beta");
        if ("release".equals(tmp)) {
            return true;
        }
        return "master".equals(tmp);
    }

    // 测试环境
    public static boolean isServerBeta() {
        String tmp = config.getProperty("version", "beta");
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
    @Override
    @Deprecated
    public String getProperty(String key, String def) {
        return config.getProperty(key, def);
    }

    /**
     * @return 返回当前是否为debug状态
     */
    public boolean isDebug() {
        if (debug == -1) {
            debug = "1".equals(config.getProperty(config_debug, "0")) ? 1 : 0;
        }
        return debug == 1;
    }
}
