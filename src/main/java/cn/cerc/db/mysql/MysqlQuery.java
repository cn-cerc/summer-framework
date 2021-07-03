package cn.cerc.db.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.RecordState;
import cn.cerc.db.core.ConnectionClient;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.core.SqlQuery;

@SuppressWarnings("serial")
public class MysqlQuery extends SqlQuery implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(MysqlQuery.class);
    private ISession session;

    public MysqlQuery() {
        super();
    }

    public MysqlQuery(IHandle handle) {
        super();
        this.session = handle.getSession();
    }

    @Override
    protected void open(boolean slaveServer) {
        this.setSlaveServer(slaveServer);
        String sql = getSqlText().getCommand();
        log.debug(sql.replaceAll("\r\n", " "));
        try (MysqlClient client = getMysqlServer().getClient()) {
            try (Statement st = client.createStatement()) {
                setFetchFinish(true);
                try (ResultSet rs = st.executeQuery(sql.replace("\\", "\\\\"))) {
                    // 取出所有数据
                    append(rs);
                    this.first();
                    this.setActive(true);
                }
            } catch (SQLException e) {
                log.error(sql);
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    // 追加相同数据表的其它记录，与已有记录合并
    public final int attach(String sql) {
        if (!this.isActive()) {
            this.clear();
            this.add(sql);
            this.open();
            return this.size();
        }

        log.debug(sql.replaceAll("\r\n", " "));
        try (MysqlClient client = getMysqlServer().getClient()) {
            try (Statement st = client.createStatement()) {
                try (ResultSet rs = st.executeQuery(sql.replace("\\", "\\\\"))) {
                    int oldSize = this.size();
                    append(rs);
                    return this.size() - oldSize;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public final void save() {
        if (!this.isBatchSave()) 
            throw new RuntimeException("batchSave is false");
        if (this.isStorage()) {
            try (MysqlClient client = getMysql().getClient()) {
                SqlOperator operator = getOperator();
                // 先执行删除
                for (Record record : delList)
                    operator.delete(client.getConnection(), record);
                // 再执行增加、修改
                this.first();
                while (this.fetch()) {
                    if (this.getCurrent().getState().equals(RecordState.dsInsert)) {
                        beforePost();
                        operator.insert(client.getConnection(), this.getCurrent());
                        afterPost();
                    } else if (this.getCurrent().getState().equals(RecordState.dsEdit)) {
                        beforePost();
                        operator.update(client.getConnection(), this.getCurrent());
                        afterPost();
                    }
                }
            }
        }
        delList.clear();
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
    protected SqlOperator getDefaultOperator() {
        return new MysqlOperator(this);
    }

    @Override
    protected ConnectionClient getConnectionClient() {
        return getMysql().getClient();
    }

    @Override
    public final ISession getSession() {
        return session;
    }

    @Override
    public final void setSession(ISession session) {
        this.session = session;
    }
}
