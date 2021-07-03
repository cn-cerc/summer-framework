package cn.cerc.db.core;

import cn.cerc.core.ISession;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mongo.MongoDB;
import cn.cerc.db.mssql.MssqlServer;
import cn.cerc.db.mysql.MysqlServerMaster;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.db.queue.QueueServer;

public class StubSession implements ISession {
    private MysqlServerMaster mysql;
    private MssqlServer mssqlConnection;
    private MongoDB mgConn;
    private QueueServer queConn;
    private OssConnection ossConn;
    private JiguangConnection pushConn;

    public StubSession() {
        mysql = new MysqlServerMaster();
        mssqlConnection = new MssqlServer();
        mgConn = new MongoDB();
        queConn = new QueueServer();
        ossConn = new OssConnection();
        pushConn = new JiguangConnection();
    }

    @Override
    public String getCorpNo() {
        throw new RuntimeException("corpNo is null");
    }

    @Override
    public String getUserCode() {
        throw new RuntimeException("userCode is null");
    }

    @Override
    public Object getProperty(String key) {
        if (MysqlServerMaster.SessionId.equals(key))
            return mysql;
        if (MssqlServer.SessionId.equals(key)) {
            return mssqlConnection;
        }
        if (MongoDB.SessionId.equals(key))
            return mgConn;
        if (QueueServer.SessionId.equals(key))
            return queConn;
        if (OssConnection.sessionId.equals(key))
            return ossConn;
        if (JiguangConnection.sessionId.equals(key))
            return pushConn;
        return null;
    }

    // 用户姓名
    @Override
    public String getUserName() {
        return getUserCode();
    }

    // 设置自定义参数
    @Override
    public void setProperty(String key, Object value) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    // 返回当前是否为已登入状态
    @Override
    public boolean logon() {
        return false;
    }

    @Override
    public void close() {
        if (mysql != null) {
            try {
                mysql.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mysql = null;
        }
    }

    @Override
    public void loadToken(String token) {
        throw new RuntimeException("not support loadToken");
    }

}
