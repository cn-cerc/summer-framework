package cn.cerc.db.mysql;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class MysqlQuery extends SqlQuery implements IHandle {
    private ISession session;

    public MysqlQuery() {
        super();
    }

    public MysqlQuery(IHandle handle) {
        super();
        this.session = handle.getSession();
    }

    @Override
    protected SqlOperator getDefaultOperator() {
        return new MysqlOperator(this);
    }

    @Override
    public final ISession getSession() {
        return session;
    }

    @Override
    public final void setSession(ISession session) {
        this.session = session;
    }

    @Override
    public final MysqlServer getServer() {
        MysqlServer master = (MysqlServer) getSession().getProperty(MysqlServerMaster.SessionId);
        if (!slaveServer)
            return master;

        MysqlServer salve = (MysqlServer) getSession().getProperty(MysqlServerSlave.SessionId);
        if (salve == null)
            return master;
        if (salve.getServer().equals(master.getServer()))
            return master;
        else
            return salve;
    }

}
