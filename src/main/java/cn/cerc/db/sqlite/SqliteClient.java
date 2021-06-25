package cn.cerc.db.sqlite;

import java.sql.Connection;

import cn.cerc.db.core.ConnectionClient;

public class SqliteClient implements ConnectionClient {
    private final Connection connection;

    public SqliteClient(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

}
