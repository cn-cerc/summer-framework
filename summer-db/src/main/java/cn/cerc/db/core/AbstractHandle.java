package cn.cerc.db.core;

import cn.cerc.core.ISession;
import cn.cerc.db.mysql.MysqlConnection;

public class AbstractHandle {

    private ISession session;

    @Deprecated
    protected AbstractHandle handle;

    public ISession getSession() {
        return session;
    }

    public void setSession(ISession session) {
        this.session = session;
    }

    public Object getProperty(String key) {
        return session.getProperty(key);
    }

    public String getCorpNo() {
        return session.getCorpNo();
    }

    public String getUserCode() {
        return session.getUserCode();
    }

    public String getUserName() {
        return session.getUserName();
    }

    public MysqlConnection getConnection() {
        return (MysqlConnection) session.getProperty(MysqlConnection.sessionId);
    }

    public boolean logon() {
        return session.logon();
    }

    @Deprecated
    public void setHandle(AbstractHandle handle) {
        this.handle = handle;
        this.session = handle.getSession();
    }

    @Deprecated
    public AbstractHandle getHandle() {
        return this.handle;
    }
}
