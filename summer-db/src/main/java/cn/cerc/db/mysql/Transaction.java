package cn.cerc.db.mysql;

import cn.cerc.db.core.IHandle;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class Transaction implements AutoCloseable {

    private Connection connection;
    private boolean active = false;
    private boolean locked = false;

    public Transaction(Connection connection) {
        setConn(connection);
    }

    public Transaction(IHandle handle) {
        MysqlConnection cn = (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
        setConn(cn.getClient());
    }
    
    private void setConn(Connection conn) {
        this.connection = conn;
        try {
            if (conn.getAutoCommit()) {
                conn.setAutoCommit(false);
                this.active = true;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean commit() {
        if (!active) {
            return false;
        }
        if (locked) {
            throw new RuntimeException("Transaction locked is true");
        }
        try {
            connection.commit();
            locked = true;
            return true;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (!active) {
            return;
        }
        try {
            try {
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean isActive() {
        return active;
    }
}
