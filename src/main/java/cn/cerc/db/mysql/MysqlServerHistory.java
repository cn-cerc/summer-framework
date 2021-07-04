package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MysqlServerHistory extends MysqlServer {
    private static final Logger log = LoggerFactory.getLogger(MysqlServerHistory.class);
    private MysqlConfig config = new MysqlConfig();

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
    public Connection createConnection() {
        // 不使用线程池直接创建
        try {
            if (getConnection() == null) {
                Class.forName(MysqlConfig.JdbcDriver);
                setConnection(
                        DriverManager.getConnection(config.getConnectUrl(), config.getUser(), config.getPassword()));
            }
            return getConnection();
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isPool() {
        return false;
    }

}
