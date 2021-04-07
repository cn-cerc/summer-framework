package cn.cerc.mis.custom;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.IConnection;
import cn.cerc.core.ISession;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mongo.MongoConnection;
import cn.cerc.db.mssql.MssqlConnection;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.db.mysql.SlaveMysqlConnection;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.db.queue.AliyunQueueConnection;
import cn.cerc.mis.core.Application;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
// @Scope(WebApplicationContext.SCOPE_REQUEST)
public class SessionDefault implements ISession {
    private static final Logger log = LoggerFactory.getLogger(SessionDefault.class);
    private Map<String, IConnection> connections = new HashMap<>();
    private Map<String, Object> params = new HashMap<>();

    public SessionDefault() {
        params.put(Application.sessionId, "");
        params.put(Application.ProxyUsers, "");
        params.put(Application.clientIP, "0.0.0.0");
        params.put(Application.userCode, "");
        params.put(Application.userName, "");
        params.put(Application.roleCode, "");
        params.put(Application.bookNo, "");
        params.put(Application.deviceLanguage, Application.App_Language);
        log.debug("new SessionDefault");
    }

    @Override
    public String getCorpNo() {
        return (String) this.getProperty(Application.bookNo);
    }

    @Override
    public boolean logon() {
        if (this.getProperty(Application.TOKEN) == null) {
            return false;
        }
        String corpNo = this.getCorpNo();
        return corpNo != null && !"".equals(corpNo);
    }

    @Override
    public Object getProperty(String key) {
        if (key == null) {
            return this;
        }

        Object result = params.get(key);
        if (result == null && !params.containsKey(key)) {
            if (connections.containsKey(key)) {
                return connections.get(key);
            }

            if (MysqlConnection.sessionId.equals(key)) {
                MysqlConnection obj = new MysqlConnection();
                connections.put(MysqlConnection.sessionId, obj);
                return connections.get(key);
            }

            if (SlaveMysqlConnection.sessionId.equals(key)) {
                SlaveMysqlConnection obj = new SlaveMysqlConnection();
                connections.put(SlaveMysqlConnection.sessionId, obj);
                return connections.get(key);
            }

            if (MssqlConnection.sessionId.equals(key)) {
                MysqlConnection obj = new MysqlConnection();
                connections.put(MysqlConnection.sessionId, obj);
                return connections.get(key);
            }

            if (OssConnection.sessionId.equals(key)) {
                OssConnection obj = new OssConnection();
                connections.put(OssConnection.sessionId, obj);
                return connections.get(key);
            }

            if (AliyunQueueConnection.sessionId.equals(key)) {
                AliyunQueueConnection obj = new AliyunQueueConnection();
                connections.put(AliyunQueueConnection.sessionId, obj);
                return connections.get(key);
            }

            if (MongoConnection.sessionId.equals(key)) {
                MongoConnection obj = new MongoConnection();
                connections.put(MongoConnection.sessionId, obj);
                return connections.get(key);
            }

            if (JiguangConnection.sessionId.equals(key)) {
                JiguangConnection obj = new JiguangConnection();
                connections.put(JiguangConnection.sessionId, obj);
                return connections.get(key);
            }
        }
        return result;
    }

    @Override
    public void setProperty(String key, Object value) {
        if (Application.TOKEN.equals(key)) {
            if ("{}".equals(value)) {
                params.put(key, null);
            } else {
                params.put(key, value);
            }
            return;
        }
        params.put(key, value);
    }

    @Override
    public String getUserName() {
        return (String) this.getProperty(Application.userName);
    }

    @Override
    public String getUserCode() {
        return (String) this.getProperty(Application.userCode);
    }

    @Override
    public void close() {
        for (String key : this.connections.keySet()) {
            Object sess = this.connections.get(key);
            try {
                if (sess instanceof AutoCloseable) {
                    ((AutoCloseable) sess).close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public MysqlConnection getConnection() {
        return (MysqlConnection) getProperty(MysqlConnection.sessionId);
    }

    public Map<String, IConnection> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, IConnection> connections) {
        this.connections = connections;
    }
}
