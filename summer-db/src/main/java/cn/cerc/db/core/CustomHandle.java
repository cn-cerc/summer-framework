package cn.cerc.db.core;

import cn.cerc.core.ISession;
import cn.cerc.db.mysql.MysqlConnection;

public class CustomHandle implements ISupportSession, AutoCloseable {

    private ISession session;

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
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

    @Override
    public void close() {
        if(session != null) {
            session.close();
        }
    }

}
