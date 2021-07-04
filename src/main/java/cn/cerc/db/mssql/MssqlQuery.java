package cn.cerc.db.mssql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.RecordState;
import cn.cerc.core.SqlText;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class MssqlQuery extends SqlQuery implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(MssqlQuery.class);
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
    protected void open(boolean slaveServer) {
        this.setSlaveServer(slaveServer);
        this.setFetchFinish(true);
        String sql = getSqlText().getCommand();
        log.debug(sql.replaceAll("\r\n", " "));
        try (MssqlClient client = getConnectionClient()){
            try (Statement st = client.getConnection().createStatement()) {
                try (ResultSet rs = st.executeQuery(sql.replace("\\", "\\\\"))) {
                    // 取出所有数据
                    append(rs);
                    this.first();
                    this.setActive(true);
                }
            }
        } catch (SQLException e) {
            log.error(sql);
            throw new RuntimeException(e.getMessage());
        }
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

    // 追加相同数据表的其它记录，与已有记录合并
    public int attach(String sql) {
        if (!this.isActive()) {
            this.clear();
            this.add(sql);
            this.open();
            return this.size();
        }

        log.debug(sql.replaceAll("\r\n", " "));
        try (MssqlClient client = getConnectionClient();){
            try (Statement st = client.getConnection().createStatement()) {
                try (ResultSet rs = st.executeQuery(sql.replace("\\", "\\\\"))) {
                    int oldSize = this.size();
                    append(rs);
                    return this.size() - oldSize;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
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
