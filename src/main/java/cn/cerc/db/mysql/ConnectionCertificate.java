package cn.cerc.db.mysql;

import java.sql.Connection;

public final class ConnectionCertificate {
    private final Connection connection;
    private final boolean poolCreated;

    public ConnectionCertificate(Connection connection, boolean poolCreated) {
        super();
        this.connection = connection;
        this.poolCreated = poolCreated;
    }

    public final Connection getConnection() {
        return connection;
    }

    public final boolean isPoolCreated() {
        return poolCreated;
    }

}