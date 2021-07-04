package cn.cerc.db.mssql;

import cn.cerc.core.SqlText;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class MssqlQuery extends SqlQuery implements IHandle {

    public MssqlQuery() {
        super();
    }

    public MssqlQuery(IHandle handle) {
        super(handle);
        this.getSqlText().setServerType(SqlText.SERVERTYPE_MSSQL);
    }

    @Override
    public MssqlServer getServer() {
        return (MssqlServer) getSession().getProperty(MssqlServer.SessionId);
    }

}
