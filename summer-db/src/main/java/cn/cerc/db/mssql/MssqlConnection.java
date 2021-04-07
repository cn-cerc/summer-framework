package cn.cerc.db.mssql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;
import cn.cerc.db.core.ServerConfig;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MssqlConnection implements IConnection, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(MssqlConnection.class);

    // IHandle中识别码
    public static final String sessionId = "mssqlSession";
    public static final String dataSource = "mssqlDataSource";

    private String url;
    private String user;
    private String password;
    private Connection connection;

    private IConfig config;

    public MssqlConnection() {
        config = ServerConfig.getInstance();
    }

    @Override
    public void close() throws Exception {
        try {
            if (connection != null) {
                log.info("close mssql connection");
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getClientId() {
        return MssqlConnection.sessionId;
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

            log.debug("mssql url {} " + url);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setConfig(IConfig config) {
        if (this.config != config) {
            url = null;
        }
        this.config = config;
    }

    public String getConnectUrl() {
        String site = config.getProperty(MssqlConfig.MSSQL_SITE, "127.0.0.1");
        String port = config.getProperty(MssqlConfig.MSSQL_PORT, "1433");
        String database = config.getProperty(MssqlConfig.MSSQL_DATABASE, "appdb");

        user = config.getProperty(MssqlConfig.MSSQL_USERNAME, "appdb_user");
        password = config.getProperty(MssqlConfig.MSSQL_PASSWORD, "appdb_password");

        if (site == null || database == null || user == null || password == null) {
            throw new RuntimeException("mssql connection error.");
        }

        // jdbc:sqlserver://112.124.37.146:1433;databaseName=MIMRC_Std;
        return String.format("jdbc:sqlserver://%s:%s;databaseName=%s", site, port, database);
    }

    public boolean execute(String sql) {
        try {
            log.info("execute mssql: ", sql);
            Statement st = getClient().createStatement();
            st.execute(sql);
            return true;
        } catch (SQLException e) {
            log.error("error mssql: {}", sql);
            throw new RuntimeException(e.getMessage());
        }
    }

}
