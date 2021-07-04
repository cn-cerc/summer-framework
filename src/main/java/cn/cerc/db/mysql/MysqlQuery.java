package cn.cerc.db.mysql;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.RecordState;
import cn.cerc.db.core.ConnectionClient;
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

    public final void save() {
        if (!this.isBatchSave())
            throw new RuntimeException("batchSave is false");
        MysqlClient client = null;
        try {
            if (this.isStorage())
                client = getMysql().getClient();
            // 先执行删除
            for (Record record : delList) {
                doBeforeDelete(record);
                if (this.isStorage())
                    getOperator().delete(client.getConnection(), record);
                doAfterDelete(record);
            }
            // 再执行增加、修改
            this.first();
            while (this.fetch()) {
                Record reccord = this.getCurrent();
                if (reccord.getState().equals(RecordState.dsInsert)) {
                    doBeforePost(reccord);
                    if (this.isStorage())
                        getOperator().insert(client.getConnection(), reccord);
                    doAfterPost(reccord);
                } else if (reccord.getState().equals(RecordState.dsEdit)) {
                    doBeforePost(reccord);
                    if (this.isStorage())
                        getOperator().update(client.getConnection(), reccord);
                    doAfterPost(reccord);
                }
            }
            delList.clear();
        } finally {
            if (client != null) {
                client.close();
                client = null;
            }
        }
    }

    @Override
    protected SqlOperator getDefaultOperator() {
        return new MysqlOperator(this);
    }

    @Override
    protected ConnectionClient getConnectionClient() {
        return getMysqlServer().getClient();
    }

    @Override
    public final ISession getSession() {
        return session;
    }

    private final MysqlServer getMysqlServer() {
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

    @Override
    public final void setSession(ISession session) {
        this.session = session;
    }
}
