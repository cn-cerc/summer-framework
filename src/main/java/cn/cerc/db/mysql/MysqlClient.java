package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import cn.cerc.db.core.ConnectionClient;

public class MysqlClient implements ConnectionClient {
    private int count = 0;
    private final MysqlServer mysql;
    private Connection connection;
    private boolean pool;

    public MysqlClient(MysqlServer mysql, boolean isPool) {
        this.mysql = mysql;
        this.pool = isPool;
    }

    public MysqlClient incReferenced() {
        if (pool) {
            ++count;
//            System.out.println("referenced count(create)= " + count);
        }
        return this;
    }

    @Override
    public void close() {
        if (pool) {
            if (--count == 0) {
                try {
                    if (connection != null) {
                        connection.close();
                        connection = null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println("referenced count(close) = " + count);
        }
    }

    @Override
    public final Connection getConnection() {
        if (connection == null) {
            if (!mysql.isPool()) {
                pool = false;
                count = 0;
            }
            this.connection = mysql.createConnection();
        }
        return connection;
    }

    public boolean isPool() {
        return pool;
    }

}
