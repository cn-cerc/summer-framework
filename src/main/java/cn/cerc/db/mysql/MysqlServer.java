package cn.cerc.db.mysql;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.resourcepool.TimeoutException;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.core.SqlServer;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class MysqlServer implements SqlServer, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(MysqlServer.class);
    // 标记栏位，为兼容历史delphi写法
    private int tag;

    public abstract ConnectionCertificate createConnection();

    public abstract String getHost();

    public abstract String getDatabase();

    @Override
    public abstract MysqlClient getClient();

    @Override
    public final boolean execute(String sql) {
        log.debug(sql);
        try (MysqlClient client = getClient()) {
            try (Statement st = client.createStatement()) {
                st.execute(sql);
                return true;
            } catch (SQLException e) {
                log.error("error sql: " + sql);
                return false;
            }
        }
    }

    public final int getTag() {
        return tag;
    }

    public final void setTag(int tag) {
        this.tag = tag;
    }

    public static final ComboPooledDataSource createDataSource(MysqlConfig config) {
        log.info("create pool to: " + config.getHost());
        // 使用线程池创建
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(MysqlConfig.JdbcDriver);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        dataSource.setJdbcUrl(config.getConnectUrl());
        dataSource.setUser(config.getUser());
        dataSource.setPassword(config.getPassword());
        // 连接池大小设置
        dataSource.setMaxPoolSize(config.getMaxPoolSize());
        dataSource.setMinPoolSize(config.getMinPoolSize());
        dataSource.setInitialPoolSize(config.getInitialPoolSize());
        // 连接池断开控制
        dataSource.setCheckoutTimeout(config.getCheckoutTimeout()); // 单位毫秒
        dataSource.setMaxIdleTime(config.getMaxIdleTime()); // 空闲自动断开时间
        // 每隔多少时间（时间请小于 数据库的 timeout）,测试一下链接，防止失效，会损失小部分性能
        dataSource.setIdleConnectionTestPeriod(config.getIdleConnectionTestPeriod()); // 单位秒
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setTestConnectionOnCheckout(false);

        return dataSource;
    }

    public static final Connection getPoolConnection(ComboPooledDataSource dataSource) {
        Connection result = null;
        try {
            result = dataSource.getConnection();
            log.debug("dataSource connection count:" + dataSource.getNumConnections());
        } catch (SQLException e) {
            if (e.getCause() instanceof InterruptedException)
                log.warn("mysql connection create timeout");
            else if (e.getCause() instanceof TimeoutException)
                log.warn("mysql connection create timeout.");
            else
                log.warn(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public SqlOperator getDefaultOperator(IHandle handle) {
        return new MysqlOperator(handle);
    }

}
