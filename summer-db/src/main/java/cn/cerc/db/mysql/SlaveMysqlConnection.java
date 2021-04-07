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
public class SlaveMysqlConnection extends SqlConnection {
    private static final ClassConfig config = new ClassConfig(SlaveMysqlConnection.class, SummerDB.ID);
    private static final Logger log = LoggerFactory.getLogger(SlaveMysqlConnection.class);

    // IHandle中识别码
    public static final String sessionId = "slaveSqlSession";
    public static String slaveDataSource = "slaveDataSource";

    private static String mysql_site;
    private static String mysql_database;
    private static String mysql_user;
    private static String mysql_pwd;

    private String url;

    static {
        mysql_site = config.getString("rds.slave.site", "127.0.0.1:3306");
        mysql_database = config.getString("rds.slave.database", "appdb");
        mysql_user = config.getString("rds.slave.username", "appdb_user");
        mysql_pwd = config.getString("rds.slave.password", "appdb_password");
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
            connection = DriverManager.getConnection(url, mysql_user, mysql_pwd);
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
        if (Utils.isEmpty(mysql_site) || Utils.isEmpty(mysql_database)) {
            throw new RuntimeException("mysql connection error");
        }
        return String.format(
                "jdbc:mysql://%s/%s?useSSL=false&autoReconnect=true&autoCommit=false&useUnicode=true&characterEncoding=utf8&serverTimezone=%s",
                mysql_site, mysql_database, MysqlConnection.getServerTimezone());
    }

    @Override
    public void setConfig(IConfig config) {
        
    }

}
