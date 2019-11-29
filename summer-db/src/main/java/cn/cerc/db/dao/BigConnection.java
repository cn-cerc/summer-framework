package cn.cerc.db.dao;

import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.mysql.MysqlConnection;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyVetoException;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class BigConnection implements Closeable {

    // 饿汉式
    private static ComboPooledDataSource dataSource;
    /**
     * 它为null表示没有事务 它不为null表示有事务 当开启事务时，需要给它赋值 当结束事务时，需要给它赋值为null
     * 并且在开启事务时，让dao的多个方法共享这个Connection
     */
    private static ThreadLocal<Connection> connections = new ThreadLocal<Connection>();
    private Connection connection;
    private boolean debugConnection = false;

    public BigConnection() {
        super();
        connection = BigConnection.popConnection();
    }

    public BigConnection(boolean debugConnection) {
        super();
        this.debugConnection = debugConnection;
        if (!debugConnection)
            connection = BigConnection.popConnection();
    }

    public synchronized static ComboPooledDataSource getDataSource() {
        if (dataSource == null) {
            ServerConfig config = ServerConfig.getInstance();

            String host = config.getProperty(MysqlConnection.rds_site, "127.0.0.1:3306");
            String db = config.getProperty(MysqlConnection.rds_database, "appdb");
            String url = String.format("jdbc:mysql://%s/%s?useSSL=false", host, db);
            String user = config.getProperty(MysqlConnection.rds_username, "appdb_user");
            String pwd = config.getProperty(MysqlConnection.rds_password, "appdb_password");
            int min_size = Integer.parseInt(config.getProperty("c3p0.min_size", "5"));
            int max_size = Integer.parseInt(config.getProperty("c3p0.max_size", "100"));
            int time_out = Integer.parseInt(config.getProperty("c3p0.time_out", "1800"));
            int max_statement = Integer.parseInt(config.getProperty("c3p0.max_statement", "100"));

            dataSource = new ComboPooledDataSource();
            try {
                dataSource.setDriverClass("com.mysql.jdbc.Driver");
            } catch (PropertyVetoException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            dataSource.setJdbcUrl(url);
            dataSource.setUser(user);
            dataSource.setPassword(pwd);

            dataSource.setMinPoolSize(min_size);
            dataSource.setMaxPoolSize(max_size);
            dataSource.setCheckoutTimeout(time_out);
            dataSource.setMaxStatements(max_statement);

            // 防止断开连接，自动测试链接是否有效
            boolean openAutoTestConn = Boolean.valueOf(config.getProperty("c3p0.open_auto_test_conn", "false"));
            if (openAutoTestConn) {
                // 每隔多少时间（时间请小于 数据库的 timeout）,测试一下链接，防止失效，会损失小部分性能
                int test_conn_time = Integer.parseInt(config.getProperty("c3p0.idle_connection_test_period", "60"));
                dataSource.setTestConnectionOnCheckin(true);
                dataSource.setTestConnectionOnCheckout(false);
                dataSource.setIdleConnectionTestPeriod(test_conn_time);
            }
        }
        return dataSource;
    }

    /**
     * dao使用本方法来获取连接
     *
     * @return 返回数据库连接
     */
    public static Connection popConnection() {
        // try {
        // System.out.println("最大连接数 " + getDataSource().getMaxPoolSize());
        // System.out.println("最小连接数 " + getDataSource().getMinPoolSize());
        // System.out.println("正在使用连接数 " + getDataSource().getNumBusyConnections());
        // System.out.println("空闲连接数 " + getDataSource().getNumIdleConnections());
        // System.out.println("总连接数 " + getDataSource().getNumConnections());
        // System.out.println("------------------");
        // } catch (SQLException e1) {
        // e1.printStackTrace();
        // }
        Connection conn = connections.get();// 获取当前线程的事务连接
        if (conn != null)
            return conn;
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启事务
     *
     * @throws SQLException SQL异常
     */
    public static void beginTransaction() throws SQLException {
        Connection con = connections.get();// 获取当前线程的事务连接
        if (con != null)
            throw new SQLException("已经开启了事务，不能重复开启！");
        con = getDataSource().getConnection();// 给con赋值，表示开启了事务
        con.setAutoCommit(false);// 设置为手动提交
        connections.set(con);// 把当前事务连接放到tl中
    }

    /**
     * 提交事务
     *
     * @throws SQLException SQL异常
     */
    public static void commitTransaction() throws SQLException {
        Connection con = connections.get();// 获取当前线程的事务连接
        if (con == null)
            throw new SQLException("没有事务不能提交！");
        con.commit();// 提交事务
        con.close();// 关闭连接
        con = null;// 表示事务结束！
        connections.remove();
    }

    /**
     * 回滚事务
     *
     * @throws SQLException SQL异常
     */
    public static void rollbackTransaction() throws SQLException {
        Connection con = connections.get();// 获取当前线程的事务连接
        if (con == null)
            throw new SQLException("没有事务不能回滚！");
        con.rollback();
        con.close();
        con = null;
        connections.remove();
    }

    /**
     * 释放Connection
     *
     * @param connection 连接对象
     * @throws SQLException Sql异常
     */
    public static void releaseConnection(Connection connection) throws SQLException {
        Connection con = connections.get();// 获取当前线程的事务连接
        if (connection != con) {// 如果参数连接，与当前事务连接不同，说明这个连接不是当前事务，可以关闭！
            if (connection != null && !connection.isClosed()) {// 如果参数连接没有关闭，关闭之！
                connection.close();
            }
        }
    }

    public Connection get() {
        if (debugConnection) {
            try {
                ServerConfig config = ServerConfig.getInstance();
                String host = config.getProperty(MysqlConnection.rds_site, "127.0.0.1:3306");
                String db = config.getProperty(MysqlConnection.rds_database, "appdb");
                String url = String.format("jdbc:mysql://%s/%s?useSSL=false", host, db);
                String user = config.getProperty(MysqlConnection.rds_username, "appdb_user");
                String pwd = config.getProperty(MysqlConnection.rds_password, "appdb_password");
                Class.forName("com.mysql.jdbc.Driver");
                if (host == null || user == null || pwd == null || db == null)
                    throw new RuntimeException("RDS配置为空，无法连接主机！");
                log.debug("create connection for mysql: " + host);
                return DriverManager.getConnection(url, user, pwd);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        } else
            return this.connection;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isDebugConnection() {
        return debugConnection;
    }

    public void setDebugConnection(boolean debugConnection) {
        this.debugConnection = debugConnection;
    }
}