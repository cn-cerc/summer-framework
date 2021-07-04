package cn.cerc.db.mssql;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.RecordState;
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

    public final void save() {
        if (!this.isBatchSave())
            throw new RuntimeException("batchSave is false");
        MssqlClient client = null;
        try {
            if (this.isStorage())
                client = getConnectionClient();
            // 先执行删除
            for (Record record : delList) {
                doBeforeDelete(record);
                if (this.isStorage()) {
                    getOperator().delete(client.getConnection(), record);
                }
                doAfterDelete(record);
            }
            // 再执行增加、修改
            this.first();
            while (this.fetch()) {
                Record record = this.getCurrent();
                if (record.getState().equals(RecordState.dsInsert)) {
                    doBeforePost(record);
                    if (this.isStorage())
                        getOperator().insert(client.getConnection(), this.getCurrent());
                    doAfterPost(record);
                } else if (record.getState().equals(RecordState.dsEdit)) {
                    doBeforePost(record);
                    if (this.isStorage())
                        getOperator().update(client.getConnection(), this.getCurrent());
                    doAfterPost(record);
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
        MssqlServer server = (MssqlServer) getSession().getProperty(MssqlServer.SessionId);
        return new MssqlClient(server.getClient());
    }

}
