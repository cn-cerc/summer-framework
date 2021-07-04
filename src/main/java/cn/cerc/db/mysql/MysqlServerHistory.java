package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MysqlServerHistory extends MysqlServer {
    private static final Logger log = LoggerFactory.getLogger(MysqlServerHistory.class);
    private MysqlConfig config = new MysqlConfig();
    private Connection connection;

    public MysqlServerHistory(String database) {
        super();
        config.setDatabase(database);
    }

    @Override
    public String getHost() {
        return config.getHost();
    }

    @Override
    public String getDatabase() {
        return config.getDatabase();
    }

    @Override
    public Connection getConnection() {
        // 不使用线程池直接创建
        try {
            if (connection == null) {
                Class.forName(MysqlConfig.JdbcDriver);
                connection = DriverManager.getConnection(config.getConnectUrl(), config.getUser(),
                        config.getPassword());
            }
            return connection;
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isPool() {
        return false;
    }

}
