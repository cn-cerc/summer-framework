package cn.cerc.db.dao;

import java.beans.PropertyVetoException;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Utils;
import cn.cerc.db.SummerDB;
import cn.cerc.db.mysql.MysqlConnection;

public class BigConnection implements Closeable {
    private static final ClassConfig config = new ClassConfig(BigConnection.class, SummerDB.ID);
    private static final Logger log = LoggerFactory.getLogger(BigConnection.class);

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
        if (!debugConnection) {
            connection = BigConnection.popConnection();
        }
    }

    public synchronized static ComboPooledDataSource getDataSource() {
        if (dataSource == null) {
            String user = MysqlConnection.getUser();
            String pwd = MysqlConnection.getPassword();
            String url = MysqlConnection.getConnectUrl(); 
            int min_size = config.getInt("c3p0.min_size", 5);
            int max_size = config.getInt("c3p0.max_size", 100);
            int time_out = config.getInt("c3p0.time_out", 1800);
            int max_statement = config.getInt("c3p0.max_statement", 100);

            dataSource = new ComboPooledDataSource();
            try {
                dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
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
            if (config.getBoolean("c3p0.open_auto_test_conn", false)) {
                // 每隔多少时间（时间请小于 数据库的 timeout）,测试一下链接，防止失效，会损失小部分性能
                int test_conn_time = config.getInt("c3p0.idle_connection_test_period", 60);
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
        Connection conn = connections.get();// 获取当前线程的事务连接
        if (conn != null) {
            return conn;
        }
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
        if (con != null) {
            throw new SQLException("sql transaction is already open, you can't open it again.");
        }
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
        if (con == null) {
            throw new SQLException("no transaction can't commit");
        }
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
        if (con == null) {
            throw new SQLException("no transaction can't rollback");
        }
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
                String url = MysqlConnection.getConnectUrl();
                String user = MysqlConnection.getUser();
                String pwd = MysqlConnection.getPassword();
                Class.forName("com.mysql.cj.jdbc.Driver");
                if (Utils.isEmpty(url) || Utils.isEmpty(user) || Utils.isEmpty(pwd)) {
                    throw new RuntimeException("mysql connection error");
                }
                log.debug("create connection for mysql: {}" , MysqlConnection.getSite());
                return DriverManager.getConnection(url, user, pwd);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return this.connection;
        }
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