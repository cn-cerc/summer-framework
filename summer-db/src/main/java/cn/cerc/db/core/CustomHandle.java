package cn.cerc.db.core;

import cn.cerc.core.ISession;
import cn.cerc.db.mysql.MysqlConnection;

public class CustomHandle implements AutoCloseable {

    private ISession session;

    @Deprecated
    protected IHandle handle;

    public ISession getSession() {
        return session;
    }

    public void setSession(ISession session) {
        this.session = session;
    }

    public void setProperty(String key, Object value) {
        session.setProperty(key, value);
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

    @Deprecated
    public boolean logon() {
        return session.logon();
    }

    @Deprecated
    public void setHandle(IHandle handle) {
        this.handle = handle;
        this.session = handle.getSession();
    }

    @Deprecated
    public IHandle getHandle() {
        return this.handle;
    }

    @Override
    public void close() {
        if(session != null) {
            session.close();
        }
    }

}
