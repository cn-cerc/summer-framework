package cn.cerc.db.mysql;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class MysqlQuery extends SqlQuery implements IHandle {
    private MysqlServer server;
    private MysqlServer master;
    private MysqlServer salve;

    public MysqlQuery() {
        super();
    }

    public MysqlQuery(IHandle handle) {
        super(handle);
    }

    @Override
    public final MysqlServer getServer() {
        if (server != null)
            return server;

        if (master == null)
            master = (MysqlServer) getSession().getProperty(MysqlServerMaster.SessionId);
        if (this.isStorage()) {
            return master;
        } else {
            if (salve == null) {
                salve = (MysqlServer) getSession().getProperty(MysqlServerSlave.SessionId);
                if (salve == null)
                    salve = master;
                if (salve.getHost().equals(master.getHost()))
                    salve = master;
            }
            return salve;
        }
    }

    public void setServer(MysqlServer server) {
        if (this.isActive())
            throw new RuntimeException("server change fail on active");
        this.server = server;
    }

}
