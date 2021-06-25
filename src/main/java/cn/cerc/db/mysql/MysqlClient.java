package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import cn.cerc.db.core.ConnectionClient;

public class MysqlClient implements ConnectionClient {
    private int count = 0;
    private final MysqlServer mysql;
    private Connection connection;
    private boolean isPool;

    public MysqlClient(MysqlServer mysql, boolean isPool) {
        this.mysql = mysql;
        this.isPool = isPool;
    }

    public MysqlClient incReferenced() {
        if (isPool) {
            ++count;
//            System.out.println("referenced count(create)= " + count);
        }
        return this;
    }

    @Override
    public void close() {
        if (isPool) {
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
            ConnectionCertificate item = mysql.createConnection();
            if (!item.isPoolCreated()) {
                isPool = false;
                count = 0;
            }
            this.connection = item.getConnection();
        }
        return connection;
    }

    public final Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

}
