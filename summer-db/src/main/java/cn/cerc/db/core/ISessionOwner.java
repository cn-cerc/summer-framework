package cn.cerc.db.core;

import cn.cerc.core.ISession;
import cn.cerc.db.mysql.MysqlConnection;

public interface ISessionOwner {

    ISession getSession();
    
    void setSession(ISession session);

    default String getCorpNo() {
        return getSession().getCorpNo();
    }

    default String getUserCode() {
        return getSession().getUserCode();
    }

    @Deprecated
    default MysqlConnection getConnection() {
        return (MysqlConnection) getSession().getProperty(MysqlConnection.sessionId);
    }

}
