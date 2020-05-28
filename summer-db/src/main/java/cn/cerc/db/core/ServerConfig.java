package cn.cerc.db.core;

import cn.cerc.core.IConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public enum ServerConfig implements IConfig {

    INSTANCE;

    // 是否为任务主机
    public static final String TaskServiceEnabled = "task.service";
    public static final String config_version = "version";
    public static final String config_debug = "debug";
    public static final String CONFIG_APP_NAME = "appName";
    private static final String confFile = "/application.properties";
    private static final Properties properties = new Properties();

    public static ServerConfig getInstance() {
        return INSTANCE;
    }

    // 是否为debug状态
    private int debug = -1;

    static {
        try {
            InputStream file = ServerConfig.class.getResourceAsStream(confFile);
            if (file != null) {
                properties.load(file);
                log.info("read from file: " + confFile);
            } else {
                log.warn("suggested use file: " + confFile);
            }
        } catch (FileNotFoundException e) {
            log.error("The settings file '" + confFile + "' does not exist.");
        } catch (IOException e) {
            log.error("Failed to load the settings from the file: " + confFile);
        }
    }

    public static boolean enableTaskService() {
        return "1".equals(getInstance().getProperty(TaskServiceEnabled, null));
    }

    public static String getAppName() {
        return getInstance().getProperty(CONFIG_APP_NAME, "localhost");
    }

    public static boolean enableDocService() {
        return "1".equals(getInstance().getProperty("docs.service", "0"));
    }

    // 正式环境
    public static boolean isServerMaster() {
        String tmp = getInstance().getProperty("version", "beta");
        if ("release".equals(tmp)) {
            return true;
        }
        return "master".equals(tmp);
    }

    public static boolean isNotMaster() {
        return !ServerConfig.isServerMaster();
    }

    // 测试环境
    public static boolean isServerBeta() {
        String tmp = getInstance().getProperty("version", "beta");
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

    @Override
    public String getProperty(String key, String def) {
        LocalConfig config = LocalConfig.getInstance();
        String result = config.getProperty(key, null);
        if (result == null) {
            result = properties.getProperty(key);
        }
        return result != null ? result : def;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * @return 返回当前是否为debug状态
     */
    public boolean isDebug() {
        if (debug == -1) {
            debug = "1".equals(this.getProperty(config_debug, "0")) ? 1 : 0;
        }
        return debug == 1;
    }
}
