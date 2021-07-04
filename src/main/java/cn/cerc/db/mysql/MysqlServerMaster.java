package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MysqlServerMaster extends MysqlServer {
    // IHandle中识别码
    public static final String SessionId = "sqlSession";
    private static ComboPooledDataSource dataSource;
    private static final MysqlConfig config;

    static {
        config = new MysqlConfig();
        if (config.getMaxPoolSize() > 0)
            dataSource = MysqlServer.createDataSource(config);
    }

    @Override
    public Connection createConnection() {
        if (isPool()) // 使用线程池创建
            return MysqlServer.getPoolConnection(dataSource);

        try {
            // 不使用线程池直接创建
            if (getConnection() == null) {
                Class.forName(MysqlConfig.JdbcDriver);
                setConnection(
                        DriverManager.getConnection(config.getConnectUrl(), config.getUser(), config.getPassword()));
            }
            return getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e.getCause());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final boolean isPool() {
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
