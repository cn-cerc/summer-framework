package cn.cerc.mis.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.IConnection;
import cn.cerc.core.ISession;
import cn.cerc.db.core.CustomBean;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mongo.MongoConnection;
import cn.cerc.db.mssql.MssqlConnection;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.db.mysql.SlaveMysqlConnection;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.db.queue.AliyunQueueConnection;
import cn.cerc.db.redis.JedisFactory;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.ServiceFactory;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
// @Scope(WebApplicationContext.SCOPE_REQUEST)
public class HandleDefault extends CustomBean implements ISession, ITokenManage {

    private Map<String, IConnection> connections = new HashMap<>();
    private Map<String, Object> params = new HashMap<>();

    public HandleDefault() {
        params.put(Application.sessionId, "");
        params.put(Application.ProxyUsers, "");
        params.put(Application.clientIP, "0.0.0.0");
        params.put(Application.userCode, "");
        params.put(Application.userName, "");
        params.put(Application.roleCode, "");
        params.put(Application.bookNo, "");
        params.put(Application.deviceLanguage, Application.App_Language);
        log.debug("new CustomBean");
        this.setSession(this);
    }

    /**
     * 根据token恢复用户session
     */
    @Override
    public boolean resumeToken(String token) {
        this.setProperty(Application.TOKEN, token);
        if (token == null)
            log.warn("initialize session, token is null");
        else
            log.info("initialize session by token {}", token);
        if (token == null) {
            return false;
        }
        if (token.length() < 10) {
            throw new RuntimeException("token 值有错！");
        }

        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionBase, token);
                Jedis redis = JedisFactory.getJedis()) {
            if (buff.isNull()) {
                buff.setField("exists", false);
                IServiceProxy svr = ServiceFactory.get(this);
                svr.setService("SvrSession.byToken");
                if (!svr.exec("token", token)) {
                    log.error("token restore error，{}", svr.getMessage());
                    this.setProperty(Application.TOKEN, null);
                    return false;
                }

                Record record = svr.getDataOut().getHead();
                buff.setField("LoginTime_", record.getDateTime("LoginTime_"));
                buff.setField("UserID_", record.getString("UserID_"));
                buff.setField("UserCode_", record.getString("UserCode_"));
                buff.setField("CorpNo_", record.getString("CorpNo_"));
                buff.setField("UserName_", record.getString("UserName_"));
                buff.setField("RoleCode_", record.getString("RoleCode_"));
                buff.setField("ProxyUsers_", record.getString("ProxyUsers_"));
                buff.setField("Language_", record.getString("Language_"));
                buff.setField("exists", true);
            }

            if (buff.getBoolean("exists")) {
                // 将用户信息赋值到句柄
                this.setProperty(Application.loginTime, buff.getDateTime("LoginTime_"));
                this.setProperty(Application.bookNo, buff.getString("CorpNo_"));
                this.setProperty(Application.userId, buff.getString("UserID_"));
                this.setProperty(Application.userCode, buff.getString("UserCode_"));
                this.setProperty(Application.userName, buff.getString("UserName_"));
                this.setProperty(Application.ProxyUsers, buff.getString("ProxyUsers_"));
                this.setProperty(Application.roleCode, buff.getString("RoleCode_"));
                this.setProperty(Application.deviceLanguage, buff.getString("Language_"));

                // 刷新缓存生命值
                redis.expire(buff.getKey(), buff.getExpires());
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 根据用户信息初始化token，并保存到缓存
     * <p>
     * 主要为 task 任务使用
     */
    @Override
    public boolean createToken(String corpNo, String userCode, String password, String machineCode) {
        String token = ApplicationConfig.getAuthToken(userCode, password, machineCode);
        if (Utils.isEmpty(token)) {
            return false;
        }
        this.setProperty(Application.TOKEN, token);
        this.setProperty(Application.bookNo, corpNo);
        this.setProperty(Application.userCode, userCode);
        this.setProperty(Application.clientIP, "0.0.0.0");

        // 将用户信息赋值到句柄
        IServiceProxy svr = ServiceFactory.get(this);
        svr.setService("SvrSession.byUserCode");
        if (!svr.exec("CorpNo_", corpNo, "UserCode_", userCode)) {
            throw new RuntimeException(svr.getMessage());
        }
        Record record = svr.getDataOut().getHead();
        this.setProperty(Application.userId, record.getString("UserID_"));
        this.setProperty(Application.loginTime, record.getDateTime("LoginTime_"));
        this.setProperty(Application.roleCode, record.getString("RoleCode_"));
        this.setProperty(Application.ProxyUsers, record.getString("ProxyUsers_"));
        this.setProperty(Application.userName, record.getString("UserName_"));
        this.setProperty(Application.deviceLanguage, record.getString("Language_"));

        // 将用户信息赋值到缓存
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionBase, token)) {
            buff.setField("LoginTime_", record.getDateTime("LoginTime_"));
            buff.setField("UserID_", record.getString("UserID_"));
            buff.setField("UserCode_", userCode);
            buff.setField("CorpNo_", corpNo);
            buff.setField("UserName_", record.getString("UserName_"));
            buff.setField("RoleCode_", record.getString("RoleCode_"));
            buff.setField("ProxyUsers_", record.getString("ProxyUsers_"));
            buff.setField("Language_", record.getString("Language_"));
            buff.setField("exists", true);
        }
        return true;
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
