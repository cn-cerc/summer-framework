package cn.cerc.db.core;

import cn.cerc.core.IConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Component
public class ServerConfig implements IConfig {

    private static final String confFile = "/application.properties";
    private static Properties properties = new Properties();
    private static ServerConfig instance;
    public static final String TaskServiceToken = "task.token";
    public static final String AdminMobile = "admin.mobile";
    public static final String AdminEmail = "admin.email";
    // 是否为debug状态
    private int debug = -1;
    // 是否为任务主机
    public static final String TaskServiceEnabled = "task.service";
    public static final String config_version = "version";
    public static final String config_debug = "debug";
    public static final String confg_appname = "appName";
    public static final int AppLevelMaster = 3;

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

    public ServerConfig() {
        if (instance != null) {
            log.error("ServerConfig instance is not null");
        }
    }

    public synchronized static ServerConfig getInstance() {
        if (instance == null) {
            instance = new ServerConfig();
        }
        return instance;
    }

    @Override
    public String getProperty(String key, String def) {
        String result = null;
        LocalConfig config = LocalConfig.getInstance();
        result = config.getProperty(key, null);
        if (result == null) {
            if (properties != null)
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

    public static ServerVersion getVersion() {
        String tmp = getInstance().getProperty(config_version, "develop");
        if ("master".equals(tmp))
            return ServerVersion.master;
        else if ("beta".equals(tmp))
            return ServerVersion.beta;
        else if ("develop".equals(tmp))
            return ServerVersion.develop;
        else
            return ServerVersion.test;
    }

    public static boolean enableTaskService() {
        return "1".equals(getInstance().getProperty(TaskServiceEnabled, null));
    }

    public static String getAppName() {
        String result = getInstance().getProperty(confg_appname, "localhost");
        return result;
    }

    public static boolean enableDocService() {
        return "1".equals(getInstance().getProperty("docs.service", "0"));
    }

    @Deprecated
    public static String getTaskToken() {
        return getInstance().getProperty(TaskServiceToken, null);
    }

    @Deprecated
    public static String wx_appid() {
        return getInstance().getProperty("wx.appid", null);
    }

    @Deprecated
    public static String wx_secret() {
        return getInstance().getProperty("wx.secret", null);
    }

    @Deprecated
    public static String dayu_serverUrl() {
        return getInstance().getProperty("dayu.serverUrl", null);
    }

    @Deprecated
    public static String dayu_appKey() {
        return getInstance().getProperty("dayu.appKey", null);
    }

    @Deprecated
    public static String dayu_appSecret() {
        return getInstance().getProperty("dayu.appSecret", null);
    }

    // 日志服务
    @Deprecated
    public static String ots_endPoint() {
        return getInstance().getProperty("ots.endPoint", null);
    }

    @Deprecated
    public static String ots_accessId() {
        return getInstance().getProperty("ots.accessId", null);
    }

    @Deprecated
    public static String ots_accessKey() {
        return getInstance().getProperty("ots.accessKey", null);
    }

    @Deprecated
    public static String ots_instanceName() {
        return getInstance().getProperty("ots.instanceName", null);
    }

    // 简讯服务(旧版本)
    @Deprecated
    public static String sms_host() {
        return getInstance().getProperty("sms.host", null);
    }

    @Deprecated
    public static String sms_username() {
        return getInstance().getProperty("sms.username", null);
    }

    @Deprecated
    public static String sms_password() {
        return getInstance().getProperty("sms.password", null);
    }

    // 微信服务
    @Deprecated
    public static String wx_host() {
        return getInstance().getProperty("wx.host", null);
    }

    @Deprecated // 请改使用 isServerMaster， isServerBeta，isServerDevelop
    public static int getAppLevel() {
        String tmp = getInstance().getProperty("version", "beta");
        if ("test".equals(tmp))
            return 1;
        if ("beta".equals(tmp))
            return 2;
        if ("release".equals(tmp))
            return AppLevelMaster;
        else
            return 0;
    }

    // 正式环境
    public static boolean isServerMaster() {
        String tmp = getInstance().getProperty("version", "beta");
        if ("release".equals(tmp))
            return true;
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
        if (isServerMaster())
            return false;
        if (isServerBeta())
            return false;
        return true;
    }

    @Deprecated
    public static int getTimeoutWarn() {
        String str = getInstance().getProperty("timeout.warn", "60");
        return Integer.parseInt(str); // 默认60秒
    }

    @Deprecated
    public static String getAdminMobile() {
        return getInstance().getProperty(AdminMobile, null);
    }

    @Deprecated
    public static String getAdminEmail() {
        return getInstance().getProperty(AdminEmail, null);
    }
}
