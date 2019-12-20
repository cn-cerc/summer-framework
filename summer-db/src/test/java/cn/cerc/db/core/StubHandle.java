package cn.cerc.db.core;

import cn.cerc.core.IConfig;
import cn.cerc.core.IHandle;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mongo.MongoConnection;
import cn.cerc.db.mssql.MssqlConnection;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.db.queue.AliyunQueueConnection;

public class StubHandle implements IHandle {
    private MysqlConnection mysqlSession;
    private MssqlConnection mssqlConnection;
    private MongoConnection mgConn;
    private AliyunQueueConnection queConn;
    private OssConnection ossConn;
    private JiguangConnection pushConn;

    public StubHandle() {
        super();
        IConfig config = new StubConfig();

        mysqlSession = new MysqlConnection();
        mysqlSession.setConfig(config);

        mssqlConnection = new MssqlConnection();
        mssqlConnection.setConfig(config);

        mgConn = new MongoConnection();
        mgConn.setConfig(config);

        queConn = new AliyunQueueConnection();
        queConn.setConfig(config);

        ossConn = new OssConnection();
        ossConn.setConfig(config);

        pushConn = new JiguangConnection();
        pushConn.setConfig(config);
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
        if (MysqlConnection.sessionId.equals(key))
            return mysqlSession;
        if (MssqlConnection.sessionId.equals(key)) {
            return mssqlConnection;
        }
        if (MongoConnection.sessionId.equals(key))
            return mgConn;
        if (AliyunQueueConnection.sessionId.equals(key))
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

    // 直接设置成登录成功状态，用于定时服务时初始化等，会生成内存临时的token
    @Override
    public boolean init(String bookNo, String userCode, String clientCode) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    // 在登录成功并生成token后，传递token值进行初始化
    @Override
    public boolean init(String token) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    // 返回当前是否为已登入状态
    @Override
    public boolean logon() {
        return false;
    }

    @Override
    public void close() {
        if (mysqlSession != null) {
            try {
                mysqlSession.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mysqlSession = null;
        }
    }

}
