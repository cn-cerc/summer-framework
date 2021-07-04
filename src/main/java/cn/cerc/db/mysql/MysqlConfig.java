package cn.cerc.db.mysql;

import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Utils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MysqlConfig {
    // mysql 连接参数：必须设置
    public static final String rds_site = "rds.site";
    public static final String rds_database = "rds.database";
    public static final String rds_username = "rds.username";
    public static final String rds_password = "rds.password";
    // mysql 连接参数：可选设置
    private static final String rds_ServerTimezone = "rds.serverTimezone";
    // 连接池相关：必须设置
    public static final String rds_MaxPoolSize = "rds.MaxPoolSize"; // default 0
    public static final String rds_MinPoolSize = "rds.MinPoolSize"; // default 9
    public static final String rds_InitialPoolSize = "rds.InitialPoolSize"; // default 3
    // 连接池相关：可选设置
    private static final String rds_CheckoutTimeout = "rds.CheckoutTimeout"; // default 500ms
    private static final String rds_MaxIdleTime = "rds.MaxIdleTime"; // default 7800s
    private static final String rds_IdleConnectionTestPeriod = "rds.IdleConnectionTestPeriod"; // default 9s
    public static final ClassConfig appConfig = new ClassConfig();
    public static final String JdbcDriver;
    private Properties config;

    static {
        JdbcDriver = appConfig.getProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
    }

    public MysqlConfig() {
        super();
        config = new Properties(appConfig.getProperties());
    }

    public String getHost() {
        return config.getProperty(rds_site, "127.0.0.1:3306");
    }

    public String getDatabase() {
        return config.getProperty(rds_database, "appdb");
    }

    public String getUser() {
        return config.getProperty(rds_username, "appdb_user");
    }

    public String getPassword() {
        return config.getProperty(rds_password, "appdb_password");
    }

    public String getServerTimezone() {
        return config.getProperty(rds_ServerTimezone, "Asia/Shanghai");
    }

    /**
     * 连接池最大连接数，默认为0（不启用），建议设置为最大并发请求数量
     * 
     * @return MaxPoolSize
     */
    public int getMaxPoolSize() {
        return Integer.parseInt(config.getProperty(rds_MaxPoolSize, "0"));
    }

    /**
     * 连接池最小连接数，默认为9，即CPU核心数*2+1
     * 
     * @return MinPoolSize
     */
    public int getMinPoolSize() {
        return Integer.parseInt(config.getProperty(rds_MinPoolSize, "9"));
    }

    /**
     * 连接池在建立时即初始化的连接数量
     * 
     * @return InitialPoolSize
     */
    public int getInitialPoolSize() {
        return Integer.parseInt(config.getProperty(rds_InitialPoolSize, "3"));
    }

    /**
     * 设置创建连接超时时间，单位为毫秒，默认为0.5秒，此值建议设置为不良体验值（当前为超出1秒即警告）的一半
     * 
     * @return CheckoutTimeout
     */
    public int getCheckoutTimeout() {
        return Integer.parseInt(config.getProperty(rds_CheckoutTimeout, "500"));
    }

    /**
     * 检查连接池中所有连接的空闲，单位为秒。注意MySQL空闲超过8小时连接自动关闭） 默认为空闲2小时即自动断开，建议其值为
     * tomcat.session的生存时长(一般设置为120分钟) 加10分钟，即130 * 60 = 7800
     * 
     * @return MaxIdleTime
     */
    public int getMaxIdleTime() {
        return Integer.parseInt(config.getProperty(rds_MaxIdleTime, "7800"));
    }

    /**
     * 检查连接池中所有空闲连接的间隔时间，单位为秒。默认为9秒，其值应比 mysql 的connect_timeout默认为10秒少1秒，即9秒
     * 
     * @return IdleConnectionTestPeriod
     */
    public int getIdleConnectionTestPeriod() {
        return Integer.parseInt(config.getProperty(rds_IdleConnectionTestPeriod, "9"));
    }

    // 以上为salve可自定义参数
    public MysqlConfig setServer(String value) {
        config.setProperty(rds_site, value);
        return this;
    }

    public MysqlConfig setDatabase(String value) {
        config.setProperty(rds_database, value);
        return this;
    }

    public MysqlConfig setUser(String value) {
        config.setProperty(rds_username, value);
        return this;
    }

    public MysqlConfig setPassword(String value) {
        config.setProperty(rds_password, value);
        return this;
    }

    public MysqlConfig setServerTimezone(String value) {
        config.setProperty(rds_ServerTimezone, value);
        return this;
    }

    public void setMaxPoolSize(String value) {
        config.setProperty(rds_MaxPoolSize, value);
    }

    public void setMinPoolSize(String value) {
        config.setProperty(rds_MinPoolSize, value);
    }

    public void setInitialPoolSize(String value) {
        config.setProperty(rds_InitialPoolSize, value);
    }

    public boolean isConfigNull() {
        return Utils.isEmpty(getHost()) || Utils.isEmpty(getDatabase()) || Utils.isEmpty(getServerTimezone());
    }

    public String getConnectUrl() {
        if (isConfigNull())
            throw new RuntimeException("mysql connection config is null");

        return String.format(
                "jdbc:mysql://%s/%s?useSSL=false&autoReconnect=true&autoCommit=false&useUnicode=true&characterEncoding=utf8&serverTimezone=%s",
                getHost(), getDatabase(), getServerTimezone());
    }

}
