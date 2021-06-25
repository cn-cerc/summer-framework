package cn.cerc.db.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.SqlText;
import cn.cerc.db.core.ConnectionClient;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class SqliteQuery extends SqlQuery {
    private static final Logger log = LoggerFactory.getLogger(SqliteQuery.class);
    private SqliteServer server = null;
    
    public SqliteQuery() {
        super();
        this.getSqlText().setServerType(SqlText.SERVERTYPE_SQLITE);
    }

    @Override
    protected void open(boolean slaveServer) {
        this.setSlaveServer(slaveServer);
        String sql = getSqlText().getCommand();
        log.debug(sql.replaceAll("\r\n", " "));
        try (Connection connection = getServer().getConnection()) {
            try (Statement state = connection.createStatement()) {
                try (ResultSet rs = state.executeQuery(sql)) {
                    // 取出所有数据
                    append(rs);
                    this.first();
                    this.setActive(true);
                }
            }
        } catch (SQLException e) {
            log.error(sql);
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected SqlOperator getDefaultOperator() {
        return new SqliteOperator();
    }

    @Override
    protected ConnectionClient getConnectionClient() {
        return getServer().getClient();
    }

    public SqliteServer getServer() {
        if(server == null)
            server = new SqliteServer();
        return server;
    }

    public void setServer(SqliteServer server) {
        this.server = server;
    }
}
