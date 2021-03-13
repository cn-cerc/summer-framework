package cn.cerc.mis.rds;

import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.db.mysql.SlaveMysqlConnection;
import cn.cerc.db.queue.AliyunQueueConnection;
import cn.cerc.mis.core.Application;
import cn.cerc.mvc.SummerMVC;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StubSession implements ISession, AutoCloseable {
    private static final ClassResource res = new ClassResource(StubSession.class, SummerMVC.ID);

    // FIXME 此处应该使用ClassConfig
    public static final String DefaultBook = "999001";
    public static final String DefaultUser = DefaultBook + "01";
    public static final String DefaultProduct = "999001000001";
    public static final String password = "123456";
    public static final String machineCode = "T800";

    // 生产部
    public static final String DefaultDept = "10050001";

    private ISession session;

    public StubSession() {
        session = Application.createSession();
        log.info("StubHandle {}", session.getClass());
        ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
        manage.createToken(DefaultBook, DefaultUser, password, machineCode);
    }

    public StubSession(String corpNo, String userCode) {
        session = Application.createSession();
        log.info("StubHandle {}", session.getClass());
        ITokenManage manage = Application.getBeanDefault(ITokenManage.class, session);
        manage.createToken(corpNo, userCode, password, machineCode);
    }

    @Override
    public String getCorpNo() {
        return session.getCorpNo();
    }

    @Override
    public String getUserCode() {
        return session.getUserCode();
    }

    @Override
    public String getUserName() {
        return session.getUserName();
    }

    @Override
    public Object getProperty(String key) {
        if ("request".equals(key)) {
            return null;
        }
        Object obj = session.getProperty(key);
        if (obj == null && MysqlConnection.sessionId.equals(key)) {
            MysqlConnection conn = new MysqlConnection();
            conn.setConfig(ServerConfig.getInstance());
            session.setProperty(key, conn);
        }
        if (obj == null && SlaveMysqlConnection.sessionId.equals(key)) {
            SlaveMysqlConnection conn = new SlaveMysqlConnection();
            conn.setConfig(ServerConfig.getInstance());
            session.setProperty(key, conn);
        }

        if (obj == null && AliyunQueueConnection.sessionId.equals(key)) {
            AliyunQueueConnection conn = new AliyunQueueConnection();
            conn.setConfig(ServerConfig.getInstance());
            session.setProperty(key, conn);
        }
        if (obj == null && JiguangConnection.sessionId.equals(key)) {
            JiguangConnection conn = new JiguangConnection();
            conn.setConfig(ServerConfig.getInstance());
            session.setProperty(key, conn);
        }
        return obj;
    }

    @Override
    public void setProperty(String key, Object value) {
        throw new RuntimeException(res.getString(1, "调用了未被实现的接口"));
    }

    @Override
    public boolean logon() {
        return false;
    }

    @Override
    public void close() {
        session.close();
    }

}
