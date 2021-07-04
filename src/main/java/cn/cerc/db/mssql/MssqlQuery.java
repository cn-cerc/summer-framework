package cn.cerc.db.mssql;

import cn.cerc.core.ISession;
import cn.cerc.core.SqlText;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class MssqlQuery extends SqlQuery implements IHandle {
    private ISession session;

    public MssqlQuery() {
        super();
    }

    public MssqlQuery(IHandle handle) {
        super();
        this.session = handle.getSession();
        this.getSqlText().setServerType(SqlText.SERVERTYPE_MSSQL);
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    @Override
    protected SqlOperator getDefaultOperator() {
        return new MssqlOperator(this);
    }

    @Override
    protected MssqlClient getConnectionClient() {
        return getServer().getClient();
    }

    public MssqlServer getServer() {
        return (MssqlServer) getSession().getProperty(MssqlServer.SessionId);
    }

}
