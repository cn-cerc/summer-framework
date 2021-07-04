package cn.cerc.db.mssql;

import java.sql.Connection;

import cn.cerc.db.core.ConnectionClient;

public class MssqlClient implements ConnectionClient {
    private final Connection connection;

    public MssqlClient(Connection connection) {
       this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() {

    }

}
