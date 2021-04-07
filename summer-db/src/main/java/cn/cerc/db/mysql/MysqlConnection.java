package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.IConfig;
import cn.cerc.core.Utils;
import cn.cerc.db.SummerDB;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MysqlConnection extends SqlConnection {
    private static final Logger log = LoggerFactory.getLogger(MysqlConnection.class);

    private static final ClassConfig config = new ClassConfig(MysqlConnection.class, SummerDB.ID);
    // IHandle中识别码
    public static final String sessionId = "sqlSession";
    @Deprecated
    public static final String rds_site = "rds.site";
    @Deprecated
    public static final String rds_database = "rds.database";
    @Deprecated
    public static final String rds_username = "rds.username";
    @Deprecated
    public static final String rds_password = "rds.password";

    public static String dataSource = "dataSource";

    private static String mysql_site;
    private static String mysql_database;
    private static String mysql_user;
    private static String mysql_password;
    private static String mysql_serverTimezone;

    private String url;

    static {
        mysql_site = config.getString("rds.site", "127.0.0.1:3306");
        mysql_database = config.getString("rds.database", "appdb");
        mysql_user = config.getString("rds.username", "appdb_user");
        mysql_password = config.getString("rds.password", "appdb_password");
        mysql_serverTimezone = config.getString("rds.serverTimezone", "Asia/Shanghai");
    }

    @Override
    public String getClientId() {
        return sessionId;
    }

    @Override
    public Connection getClient() {
        if (connection != null) {
            return connection;
        }

        try {
            if (url == null) {
                url = getConnectUrl();
            }
            log.debug("create connection for mysql: " + url);
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, mysql_user, mysql_password);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean execute(String sql) {
        try {
            log.debug(sql);
            Statement st = getClient().createStatement();
            st.execute(sql);
            return true;
        } catch (SQLException e) {
            log.error("error sql: " + sql);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String getConnectUrl() {
        if (Utils.isEmpty(mysql_site) || Utils.isEmpty(mysql_database) || Utils.isEmpty(mysql_serverTimezone)) {
            throw new RuntimeException("mysql connection error");
        }
        return String.format(
                "jdbc:mysql://%s/%s?useSSL=false&autoReconnect=true&autoCommit=false&useUnicode=true&characterEncoding=utf8&serverTimezone=%s",
                mysql_site, mysql_database, mysql_serverTimezone);
    }

    public String getDatabase() {
        return mysql_database;
    }

    public static String getSite() {
        return mysql_site;
    }

    public static String getUser() {
        return mysql_user;
    }

    public static String getPassword() {
        return mysql_password;
    }

    public static String getServerTimezone() {
        return mysql_serverTimezone;
    }

    @Override
    public void setConfig(IConfig config) {

    }

}
