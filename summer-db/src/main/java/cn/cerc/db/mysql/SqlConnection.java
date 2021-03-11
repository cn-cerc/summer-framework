package cn.cerc.db.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import cn.cerc.core.IConfig;
import cn.cerc.core.ISqlConnection;
import cn.cerc.db.core.ServerConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SqlConnection implements ISqlConnection, AutoCloseable {
    protected Connection connection;
    protected IConfig config;
    private int tag;

    public SqlConnection() {
        config = ServerConfig.getInstance();
    }

    @Override
    public void close() throws Exception {
        try {
            if (connection != null) {
                log.debug("close connection.");
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract boolean execute(String sql);

    public IConfig getConfig() {
        return config;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

}
