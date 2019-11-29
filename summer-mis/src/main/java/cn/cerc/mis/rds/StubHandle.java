package cn.cerc.mis.rds;

import cn.cerc.core.IHandle;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.db.mysql.SqlConnection;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.db.queue.AliyunQueueConnection;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;

public class StubHandle implements IHandle, AutoCloseable {
    public static final String DefaultBook = "999001";
    public static final String DefaultUser = DefaultBook + "01";
    public static final String DefaultProduct = "999001000001";
    // 生产部
    public static final String DefaultDept = "10050001";
    private IHandle handle;
    private static final String clientIP = "127.0.0.1";

    public StubHandle() {
        handle = Application.getHandle();
        handle.init(DefaultBook, DefaultUser, clientIP);
    }

    public StubHandle(String corpNo, String userCode) {
        handle = Application.getHandle();
        handle.init(corpNo, userCode, clientIP);
    }

    @Deprecated
    public StubHandle(String corpNo) {
        handle = Application.getHandle();
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery ds = new SqlQuery(this);
        ds.setMaximum(1);
        ds.add("select Code_ from " + systemTable.getUserInfo());
        ds.add("where CorpNo_='%s'", corpNo);
        ds.open();
        if (ds.eof())
            throw new RuntimeException("找不到默认帐号：CorpNo=" + corpNo);
        String userCode = ds.getString("Code_");

        handle.init(corpNo, userCode, clientIP);
    }

    public SqlConnection getConnection() {
        return (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
    }

    @Override
    public String getCorpNo() {
        return handle.getCorpNo();
    }

    @Override
    public String getUserCode() {
        return handle.getUserCode();
    }

    @Override
    public String getUserName() {
        return handle.getUserName();
    }

    @Override
    public Object getProperty(String key) {
        if ("request".equals(key))
            return null;
        Object obj = handle.getProperty(key);
        if (obj == null && MysqlConnection.sessionId.equals(key)) {
            MysqlConnection conn = new MysqlConnection();
            conn.setConfig(ServerConfig.getInstance());
            handle.setProperty(key, conn);
        }
        if (obj == null && AliyunQueueConnection.sessionId.equals(key)) {
            AliyunQueueConnection conn = new AliyunQueueConnection();
            conn.setConfig(ServerConfig.getInstance());
            handle.setProperty(key, conn);
        }
        if (obj == null && JiguangConnection.sessionId.equals(key)) {
            JiguangConnection conn = new JiguangConnection();
            conn.setConfig(ServerConfig.getInstance());
            handle.setProperty(key, conn);
        }
        return obj;
    }

    @Override
    public void setProperty(String key, Object value) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    @Override
    public boolean init(String bookNo, String userCode, String clientCode) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    @Override
    public boolean init(String token) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    @Override
    public boolean logon() {
        return false;
    }

    @Override
    public void close() {
        handle.close();
    }

}
