package cn.cerc.mis.core;

import cn.cerc.core.IConnection;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.db.core.Curl;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mongo.MongoConnection;
import cn.cerc.db.mssql.MssqlConnection;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.db.mysql.SlaveMysqlConnection;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.db.queue.AliyunQueueConnection;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.client.RemoteService;
import cn.cerc.mis.client.ServiceFactory;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.rds.StubHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
// @Scope(WebApplicationContext.SCOPE_REQUEST)
public class HandleDefault implements IHandle {

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
        log.debug("new CustomHandle");
    }

    /**
     * 根据token恢复用户session
     */
    @Override
    public boolean init(String token) {
        this.setProperty(Application.token, token);
        log.info("根据 token={} 初始化 session", token);
        if (token == null) {
            return false;
        }
        if (token.length() < 10) {
            throw new RuntimeException("token 值有错！");
        }

        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionBase, token)) {
            if (buff.isNull()) {
                buff.setField("exists", false);
                IServiceProxy svr = ServiceFactory.get(this);
                svr.setService("SvrSession.byToken");
                if (!svr.exec("token", token)) {
                    log.error("token 恢复错误，原因 {}", svr.getMessage());
                    this.setProperty(Application.token, null);
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
    public boolean init(String corpNo, String userCode, String clientIP) {
        String token = ApplicationConfig.generateToken();
        log.info("根据用户 {}，创建新的token {}", userCode, token);

        // 回算用户注册 token
        if (StubHandle.DefaultUser.equals(userCode)) {
            registerToken(userCode, token);
        }

        this.setProperty(Application.token, token);
        this.setProperty(Application.bookNo, corpNo);
        this.setProperty(Application.userCode, userCode);
        this.setProperty(Application.clientIP, clientIP);

        // 将用户信息赋值到句柄
        IServiceProxy svr = ServiceFactory.get(this);
        svr.setService("SvrSession.byUserCode");
        if (!svr.exec("userCode", userCode, "token", token)) {
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

    /**
     * 注册token信息到中央数据库
     */
    private void registerToken(String userCode, String token) {
        Curl curl = new Curl();
        curl.put("userCode", userCode).put("token", token).put("machine", ServerConfig.getAppName());

        String host = RemoteService.getApiHost(ServiceFactory.Public);
        String site = host + ApplicationConfig.App_Path + "ApiTaskToken.register";
        String response = curl.doPost(site);
        log.warn("token {} 注册结果 {}", token, response);
    }

    @Override
    public String getCorpNo() {
        return (String) this.getProperty(Application.bookNo);
    }

    @Override
    public boolean logon() {
        if (this.getProperty(Application.token) == null) {
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
        if (Application.token.equals(key)) {
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
