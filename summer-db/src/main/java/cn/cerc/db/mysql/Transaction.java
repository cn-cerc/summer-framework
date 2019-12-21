package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import cn.cerc.core.IHandle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Transaction implements AutoCloseable {

    private Connection conn;
    private boolean active = false;
    private boolean locked = false;

    public Transaction(Connection conn) {
        this.conn = conn;
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

    public Transaction(IHandle handle) {
        MysqlConnection cn = (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
        this.conn = cn.getClient();
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
        if (!active)
            return false;
        if (locked)
            throw new RuntimeException("Transaction locked is true");
        try {
            conn.commit();
            locked = true;
            return true;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (!active)
            return;
        try {
            try {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
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
