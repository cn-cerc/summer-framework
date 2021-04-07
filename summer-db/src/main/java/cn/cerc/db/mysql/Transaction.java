package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.db.core.ISessionOwner;

public class Transaction implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(Transaction.class);

    private Connection connection;
    private boolean active = false;
    private boolean locked = false;

    public Transaction(Connection connection) {
        setConn(connection);
    }

    public Transaction(ISession session) {
        MysqlConnection cn = (MysqlConnection) session.getProperty(MysqlConnection.sessionId);
        setConn(cn.getClient());
    }

    public Transaction(ISessionOwner owner) {
        this(owner.getSession());
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
