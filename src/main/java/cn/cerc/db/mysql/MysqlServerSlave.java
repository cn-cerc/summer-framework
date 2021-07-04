package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import cn.cerc.core.ClassConfig;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MysqlServerSlave extends MysqlServer {
    // IHandle中识别码
    public static final String SessionId = "slaveSqlSession";
    private static final Logger log = LoggerFactory.getLogger(MysqlServerSlave.class);
    private static ComboPooledDataSource dataSource;
    private static final MysqlConfig config;

    static {
        config = new MysqlConfig();

        final String salve = ".salve";
        final ClassConfig appConfig = MysqlConfig.appConfig;

        // mysql 连接相关，在未设置时，将与master库相同
        final String server = config.getHost();
        final String database = config.getDatabase();
        final String user = config.getUser();
        final String password = config.getPassword();
        config.setServer(appConfig.getString(MysqlConfig.rds_site + salve, server));
        config.setDatabase(appConfig.getString(MysqlConfig.rds_database + salve, database));
        config.setUser(appConfig.getString(MysqlConfig.rds_username + salve, user));
        config.setPassword(appConfig.getString(MysqlConfig.rds_password + salve, password));

        // mysql 连接池相关
        config.setMaxPoolSize(appConfig.getString(MysqlConfig.rds_MaxPoolSize + salve, "0"));
        config.setMinPoolSize(appConfig.getString(MysqlConfig.rds_MinPoolSize + salve, "9"));
        config.setInitialPoolSize(appConfig.getString(MysqlConfig.rds_InitialPoolSize + salve, "3"));

        if (config.getMaxPoolSize() > 0)
            dataSource = MysqlServer.createDataSource(config);
    }

    @Override
    public Connection createConnection() {
        if (isPool()) // 使用线程池创建
            return MysqlServer.getPoolConnection(dataSource);

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
        return dataSource != null;
    }

    @Override
    public String getHost() {
        return config.getHost();
    }

    @Override
    public String getDatabase() {
        return config.getDatabase();
    }

    public static void openPool() {

    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

}
