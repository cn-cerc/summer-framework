package cn.cerc.db.core;

import java.sql.ResultSet;
import java.sql.Statement;

import cn.cerc.core.ISession;
import cn.cerc.db.mysql.MysqlClient;
import cn.cerc.db.mysql.MysqlServerMaster;

public interface IHandle {

    ISession getSession();

    void setSession(ISession session);

    default String getCorpNo() {
        return getSession().getCorpNo();
    }

    default String getUserCode() {
        return getSession().getUserCode();
    }

    default MysqlServerMaster getMysql() {
        return (MysqlServerMaster) getSession().getProperty(MysqlServerMaster.SessionId);
    }

    /**
     * 若执行sql指令后，有返回一条或一条记录以上，则为true，否则为false;
     * 
     * @param sql sql执行语句
     * 
     * @return database exit
     */
    default boolean DBExists(String sql) {
        try (MysqlClient client = getMysql().getClient()) {
            try (Statement st = client.getConnection().createStatement()) {
                try (ResultSet rs = st.executeQuery(sql)) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Deprecated
    default void setProperty(String key, Object value) {
        getSession().setProperty(key, value);
    }

    @Deprecated
    default void setHandle(IHandle handle) {
        this.setSession(handle.getSession());
    }
}
