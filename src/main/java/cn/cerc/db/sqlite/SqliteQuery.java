package cn.cerc.db.sqlite;

import cn.cerc.core.SqlText;
import cn.cerc.db.core.ConnectionClient;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class SqliteQuery extends SqlQuery {
    private SqliteServer server = null;

    public SqliteQuery() {
        super();
        this.getSqlText().setServerType(SqlText.SERVERTYPE_SQLITE);
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
        if (server == null)
            server = new SqliteServer();
        return server;
    }

    public void setServer(SqliteServer server) {
        this.server = server;
    }
}
