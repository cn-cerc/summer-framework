package cn.cerc.db.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.DataSetEvent;
import cn.cerc.core.DataSetState;
import cn.cerc.core.FieldDefs;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.SqlText;
import cn.cerc.db.mysql.BigdataException;
import cn.cerc.db.mysql.MysqlServerMaster;
import cn.cerc.db.mysql.MysqlServer;
import cn.cerc.db.mysql.MysqlServerSlave;
import cn.cerc.db.mysql.SqlClient;
import cn.cerc.db.mysql.SqlOperator;

@SuppressWarnings("serial")
public class MysqlQuery extends DataSet implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(MysqlQuery.class);
    private ISession session;
    // 数据集是否有打开
    private boolean active = false;
    // 使用只读数据源
    private boolean slaveServer;
    // 若数据有取完，则为true，否则为false
    private boolean fetchFinish;
    // 仅当batchSave为true时，delList才有记录存在
    private List<Record> delList = new ArrayList<>();
    // 数据库保存操作执行对象
    private SqlOperator operator;
    // 批次保存模式，默认为post与delete立即保存
    private boolean batchSave = false;
    // sqlcommand指令
    private SqlText sqlText = new SqlText();

    public MysqlQuery(IHandle handle) {
        this.session = handle.getSession();
    }

    @Override
    public void close() {
        this.active = false;
        this.operator = null;
        super.close();
    }

    public MysqlQuery open() {
        return this.open(false);
    }

    public MysqlQuery openReadonly() {
        return this.open(true);
    }

    public MysqlQuery open(boolean slaveServer) {
        this.setSlaveServer(slaveServer);
        String sql = getSqlText().getCommand();
        log.debug(sql.replaceAll("\r\n", " "));
        try (SqlClient client = getMysqlServer().getClient()) {
            try (Statement st = client.createStatement()) {
                this.fetchFinish = true;
                try (ResultSet rs = st.executeQuery(sql.replace("\\", "\\\\"))) {
                    // 取出所有数据
                    append(rs);
                    this.first();
                    this.active = true;
                    return this;
                }
            } catch (SQLException e) {
                log.error(sql);
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    // 追加相同数据表的其它记录，与已有记录合并
    public final int attach(String sql) {
        if (!this.active) {
            this.clear();
            this.add(sql);
            this.open();
            return this.size();
        }

        log.debug(sql.replaceAll("\r\n", " "));
        try (SqlClient client = getMysqlServer().getClient()) {
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

    private void append(ResultSet rs) throws SQLException {
        DataSetEvent onAfterAppend = this.getOnAfterAppend();
        try {
            this.setOnAfterAppend(null);
            rs.last();
            if (getSqlText().getMaximum() > -1) {
                BigdataException.check(this, this.size() + rs.getRow());
            }
            // 取得字段清单
            ResultSetMetaData meta = rs.getMetaData();
            FieldDefs defs = this.getFieldDefs();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String field = meta.getColumnLabel(i);
                if (!defs.exists(field)) {
                    defs.add(field);
                }
            }
            // 取得所有数据
            if (rs.first()) {
                int total = this.size();
                do {
                    total++;
                    if (this.getMaximum() > -1 && this.getMaximum() < total) {
                        this.fetchFinish = false;
                        break;
                    }
                    Record record = this.newRecord();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String fn = rs.getMetaData().getColumnLabel(i);
                        record.setField(fn, rs.getObject(fn));
                    }
                    record.setState(DataSetState.dsNone);
                    this.append(record);
                } while (rs.next());
            }
        } finally {
            this.setOnAfterAppend(onAfterAppend);
        }
    }

    @Override
    public final void delete() {
        Record record = this.getCurrent();
        super.delete();
        if (record.getState() == DataSetState.dsInsert) {
            return;
        }
        if (this.isBatchSave()) {
            delList.add(record);
        } else {
            try (SqlClient client = getMysql().getClient()) {
                getDefaultOperator().delete(client.getConnection(), record);
            }
        }
    }

    @Override
    public final void post() {
        if (this.isBatchSave()) {
            return;
        }
        Record record = this.getCurrent();
        if (record.getState() == DataSetState.dsInsert) {
            beforePost();
            try (SqlClient client = getMysql().getClient()) {
                getDefaultOperator().insert(client.getConnection(), record);
            }
            super.post();
        } else if (record.getState() == DataSetState.dsEdit) {
            beforePost();
            try (SqlClient client = getMysql().getClient()) {
                getDefaultOperator().update(client.getConnection(), record);
            }
            super.post();
        }
    }

    public final void save() {
        if (!this.isBatchSave()) {
            throw new RuntimeException("batchSave is false");
        }
        SqlOperator operator = getDefaultOperator();
        try (SqlClient client = getMysql().getClient()) {
            // 先执行删除
            for (Record record : delList) {
                operator.delete(client.getConnection(), record);
            }
            delList.clear();
            // 再执行增加、修改
            this.first();
            while (this.fetch()) {
                if (this.getState().equals(DataSetState.dsInsert)) {
                    beforePost();
                    operator.insert(client.getConnection(), this.getCurrent());
                    super.post();
                } else if (this.getState().equals(DataSetState.dsEdit)) {
                    beforePost();
                    operator.update(client.getConnection(), this.getCurrent());
                    super.post();
                }
            }
        }
    }

    public final SqlOperator getDefaultOperator() {
        if (operator == null) {
            SqlOperator def = new SqlOperator(this);
            String sql = this.getSqlText().getText();
            if (sql != null) {
                def.setTableName(SqlOperator.findTableName(sql));
            }
            operator = def;
        }
        SqlOperator opear = operator;
        if (opear.getTableName() == null) {
            String sql = this.getSqlText().getText();
            if (sql != null) {
                opear.setTableName(SqlOperator.findTableName(sql));
            }
        }
        return operator;
    }

    public final SqlOperator getOperator() {
        return operator;
    }

    public final void setOperator(SqlOperator operator) {
        this.operator = operator;
    }

    // 是否批量保存
    public final boolean isBatchSave() {
        return batchSave;
    }

    public final void setBatchSave(boolean batchSave) {
        this.batchSave = batchSave;
    }

    /**
     * 增加sql指令内容，调用此函数需要自行解决sql注入攻击！
     *
     * @param sql 要增加的sql指令内容
     * @return 返回对象本身
     */
    public final MysqlQuery add(String sql) {
        sqlText.add(sql);
        return this;
    }

    public final MysqlQuery add(String format, Object... args) {
        sqlText.add(format, args);
        return this;
    }

    public final String getCommandText() {
        return this.sqlText.getText();
    }

    @Deprecated
    public final SqlText getSqlText() {
        return this.sqlText;
    }

    public final void setSqlText(SqlText sqlText) {
        this.sqlText = sqlText;
    }

    @Deprecated // 请改使用 getSqlText().clear
    public final MysqlQuery emptyCommand() {
        this.sqlText.clear();
        return this;
    }

    public final boolean getActive() {
        return active;
    }

    public final MysqlQuery setActive(boolean value) {
        if (value) {
            if (!this.active) {
                this.open();
            }
            this.active = true;
        } else {
            this.close();
        }
        return this;
    }

    @Override
    public final ISession getSession() {
        return session;
    }

    @Override
    public final void setSession(ISession session) {
        this.session = session;
    }

    public final boolean isSlaveServer() {
        return slaveServer;
    }

    public final void setSlaveServer(boolean slaveServer) {
        this.slaveServer = slaveServer;
    }

    public final MysqlServer getMysqlServer() {
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

    public final void clear() {
        close();
        this.getSqlText().clear();
    }

    public final int getMaximum() {
        return getSqlText().getMaximum();
    }

    public final MysqlQuery setMaximum(int maximum) {
        getSqlText().setMaximum(maximum);
        return this;
    }

    public final boolean getFetchFinish() {
        return fetchFinish;
    }

    @Override
    public String toString() {
        StringBuffer sl = new StringBuffer();
        sl.append(String.format("[%s]%n", this.getClass().getName()));
        sl.append(String.format("CommandText:%s%n", this.getSqlText().getText()));
        sl.append(String.format("RecordCount:%d%n", this.size()));
        sl.append(String.format("RecNo:%d%n", this.getRecNo()));
        return sl.toString();
    }
}
