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

    public static final String TaskServiceToken = "task.token";
    public static final String AdminMobile = "admin.mobile";
    public static final String AdminEmail = "admin.email";

    // 是否为任务主机
    public static final String TaskServiceEnabled = "task.service";
    public static final String config_version = "version";
    public static final String config_debug = "debug";
    public static final String confg_appname = "appName";
    public static final int AppLevelMaster = 3;
    private static final String confFile = "/application.properties";
    private static Properties properties = new Properties();

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
        return "1".equals(INSTANCE.getProperty(TaskServiceEnabled, null));
    }

    public static String getAppName() {
        String result = INSTANCE.getProperty(confg_appname, "localhost");
        return result;
    }

    public static boolean enableDocService() {
        return "1".equals(INSTANCE.getProperty("docs.service", "0"));
    }

    @Deprecated
    public static String getTaskToken() {
        return INSTANCE.getProperty(TaskServiceToken, null);
    }

    @Deprecated
    public static String wx_appid() {
        return INSTANCE.getProperty("wx.appid", null);
    }

    @Deprecated
    public static String wx_secret() {
        return INSTANCE.getProperty("wx.secret", null);
    }

    @Deprecated
    public static String dayu_serverUrl() {
        return INSTANCE.getProperty("dayu.serverUrl", null);
    }

    @Deprecated
    public static String dayu_appKey() {
        return INSTANCE.getProperty("dayu.appKey", null);
    }

    @Deprecated
    public static String dayu_appSecret() {
        return INSTANCE.getProperty("dayu.appSecret", null);
    }

    // 简讯服务(旧版本)
    @Deprecated
    public static String sms_host() {
        return INSTANCE.getProperty("sms.host", null);
    }

    @Deprecated
    public static String sms_username() {
        return INSTANCE.getProperty("sms.username", null);
    }

    @Deprecated
    public static String sms_password() {
        return INSTANCE.getProperty("sms.password", null);
    }

    // 微信服务
    @Deprecated
    public static String wx_host() {
        return INSTANCE.getProperty("wx.host", null);
    }

    @Deprecated // 请改使用 isServerMaster， isServerBeta，isServerDevelop
    public static int getAppLevel() {
        String tmp = INSTANCE.getProperty("version", "beta");
        if ("test".equals(tmp)) {
            return 1;
        }
        if ("beta".equals(tmp)) {
            return 2;
        }
        if ("release".equals(tmp)) {
            return AppLevelMaster;
        } else {
            return 0;
        }
    }

    // 正式环境
    public static boolean isServerMaster() {
        String tmp = INSTANCE.getProperty("version", "beta");
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
        String tmp = INSTANCE.getProperty("version", "beta");
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

    @Deprecated
    public static int getTimeoutWarn() {
        String str = INSTANCE.getProperty("timeout.warn", "60");
        return Integer.parseInt(str); // 默认60秒
    }

    @Deprecated
    public static String getAdminMobile() {
        return INSTANCE.getProperty(AdminMobile, null);
    }

    @Deprecated
    public static String getAdminEmail() {
        return INSTANCE.getProperty(AdminEmail, null);
    }

    @Override
    public String getProperty(String key, String def) {
        String result = null;
        LocalConfig config = LocalConfig.INSTANCE;
        result = config.getProperty(key, null);
        if (result == null) {
            if (properties != null) {
                result = properties.getProperty(key);
            }
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
