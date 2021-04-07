package cn.cerc.db.core;

import cn.cerc.db.mysql.MysqlConnection;

public interface IHandle extends ISessionOwner {

    default String getCorpNo() {
        return getSession().getCorpNo();
    }

    default String getUserCode() {
        return getSession().getUserCode();
    }

    default MysqlConnection getConnection() {
        return (MysqlConnection) getSession().getProperty(MysqlConnection.sessionId);
    }

}
