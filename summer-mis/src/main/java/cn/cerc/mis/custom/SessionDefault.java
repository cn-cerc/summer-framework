package cn.cerc.mis.custom;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.core.LanguageResource;
import cn.cerc.core.Record;
import cn.cerc.db.core.Handle;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mongo.MongoDB;
import cn.cerc.db.mssql.MssqlServer;
import cn.cerc.db.mysql.MysqlServerMaster;
import cn.cerc.db.mysql.MysqlServerSlave;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.db.queue.QueueServer;
import cn.cerc.db.redis.JedisFactory;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CenterService;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

@Component
//@Scope(WebApplicationContext.SCOPE_REQUEST)
//@Scope(WebApplicationContext.SCOPE_SESSION)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionDefault implements ISession {
    public static final String TOKEN_CREATE_ENTER = "TOKEN_CREATE_STATUS";
    private static final Logger log = LoggerFactory.getLogger(SessionDefault.class);
    private Map<String, Object> connections = new HashMap<>();
    private Map<String, Object> params = new HashMap<>();
    private static int currentSize = 0;

    public SessionDefault() {
        params.put(Application.SessionId, "");
        params.put(Application.ProxyUsers, "");
        params.put(Application.ClientIP, "0.0.0.0");
        params.put(ISession.USER_CODE, "");
        params.put(ISession.USER_NAME, "");
        params.put(ISession.CORP_NO, "");
        params.put(ISession.LANGUAGE_ID, LanguageResource.appLanguage);
        log.debug("new SessionDefault");
        synchronized (this.getClass()) {
            ++currentSize;
//            log.info("current size: {}", currentSize);
        }
    }

    @Override
    public String getCorpNo() {
        return (String) this.getProperty(ISession.CORP_NO);
    }

    @Override
    public boolean logon() {
        if (this.getProperty(ISession.TOKEN) == null) {
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

            if (MysqlServerMaster.SessionId.equals(key)) {
                MysqlServerMaster obj = new MysqlServerMaster();
                connections.put(MysqlServerMaster.SessionId, obj);
                return connections.get(key);
            }

            if (MysqlServerSlave.SessionId.equals(key)) {
                MysqlServerSlave obj = new MysqlServerSlave();
                connections.put(MysqlServerSlave.SessionId, obj);
                return connections.get(key);
            }

            if (MssqlServer.SessionId.equals(key)) {
                MysqlServerMaster obj = new MysqlServerMaster();
                connections.put(MysqlServerMaster.SessionId, obj);
                return connections.get(key);
            }

            if (OssConnection.sessionId.equals(key)) {
                OssConnection obj = new OssConnection();
                connections.put(OssConnection.sessionId, obj);
                return connections.get(key);
            }

            if (QueueServer.SessionId.equals(key)) {
                QueueServer obj = new QueueServer();
                connections.put(QueueServer.SessionId, obj);
                return connections.get(key);
            }

            if (MongoDB.SessionId.equals(key)) {
                MongoDB obj = new MongoDB();
                connections.put(MongoDB.SessionId, obj);
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
        if (ISession.TOKEN.equals(key)) {
            if ("{}".equals(value)) {
                params.put(key, null);
            } else {
                if (value == null || "".equals(value))
                    params.clear();
                else {
                    params.put(key, value);
                    if (params.get(SessionDefault.TOKEN_CREATE_ENTER) == null)
                        init((String) value);
                }
            }
            return;
        }
        params.put(key, value);
    }

    @Override
    public String getUserName() {
        return (String) this.getProperty(ISession.USER_NAME);
    }

    @Override
    public String getUserCode() {
        return (String) this.getProperty(ISession.USER_CODE);
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
        connections.clear();
        synchronized (this.getClass()) {
            --currentSize;
//            log.info("current size: {}", currentSize);
        }
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void init(String token) {
        if (token.length() < 10) {
            throw new RuntimeException("token value error: length < 10");
        }

        try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.Token.SessionBase, token);
                Jedis redis = JedisFactory.getJedis()) {
            if (buff.isNull()) {
                CenterService svr = new CenterService(new Handle(this));
                svr.setService("SvrSession.byToken");
                if (!svr.exec("token", token)) {
                    log.debug("token restore error：{}", svr.getMessage());
                    params.put(ISession.TOKEN, null);
                    return;
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
                params.put(Application.LoginTime, buff.getDateTime("LoginTime_"));
                params.put(ISession.CORP_NO, buff.getString("CorpNo_"));
                params.put(Application.UserId, buff.getString("UserID_"));
                params.put(ISession.USER_CODE, buff.getString("UserCode_"));
                params.put(ISession.USER_NAME, buff.getString("UserName_"));
                params.put(Application.ProxyUsers, buff.getString("ProxyUsers_"));
                params.put(ISession.LANGUAGE_ID, buff.getString("Language_"));

                // 刷新缓存生命值
                if (redis != null)
                    redis.expire(buff.getKey(), buff.getExpires());
            }
        }
    }

}
