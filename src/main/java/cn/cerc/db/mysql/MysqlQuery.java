package cn.cerc.db.mysql;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class MysqlQuery extends SqlQuery implements IHandle {

    public MysqlQuery() {
        super();
    }

    public MysqlQuery(IHandle handle) {
        super(handle);
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
