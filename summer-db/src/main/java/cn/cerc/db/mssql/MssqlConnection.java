package cn.cerc.db.mssql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MssqlConnection implements IConnection, AutoCloseable {

    private String url;
    private String user;
    private String password;
    protected Connection connection;
    protected IConfig config;

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
        return MssqlConfig.Mssql_Client_Id;
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
        String site = config.getProperty(MssqlConfig.Mssql_Site, "127.0.0.1");
        String port = config.getProperty(MssqlConfig.Mssql_Port, "1433");
        String database = config.getProperty(MssqlConfig.Mssql_Database, "appdb");

        user = config.getProperty(MssqlConfig.Mssql_Username, "appdb_user");
        password = config.getProperty(MssqlConfig.Mssql_Password, "appdb_password");

        if (site == null || database == null || user == null || password == null) {
            throw new RuntimeException("RDS配置为空，无法连接主机！");
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
