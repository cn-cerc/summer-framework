package cn.cerc.mis.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.IConnection;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.jiguang.JiguangConnection;
import cn.cerc.db.mongo.MongoConnection;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.db.mysql.SlaveMysqlConnection;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.db.queue.AliyunQueueConnection;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
// @Scope(WebApplicationContext.SCOPE_REQUEST)
public class HandleDefault implements IHandle {
    private static final Logger log = LoggerFactory.getLogger(HandleDefault.class);
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
        params.put(Application.deviceLanguage, Application.LangageDefault);
        log.debug("new CustomHandle");
    }

    @Override
    public boolean init(String corpNo, String userCode, String clientIP) {
        String token = GuidFixStr(Utils.newGuid());
        this.setProperty(Application.token, token);
        this.setProperty(Application.bookNo, corpNo);
        this.setProperty(Application.userCode, userCode);
        this.setProperty(Application.clientIP, clientIP);

        LocalService svr = new LocalService(this, "AppSessionRestore.byUserCode");
        if (!svr.exec("userCode", userCode)) {
            throw new RuntimeException(svr.getMessage());
        }
        Record headOut = svr.getDataOut().getHead();
        this.setProperty(Application.userId, headOut.getString("UserID_"));
        this.setProperty(Application.loginTime, headOut.getDateTime("LoginTime_"));
        this.setProperty(Application.roleCode, headOut.getString("RoleCode_"));
        this.setProperty(Application.ProxyUsers, headOut.getString("ProxyUsers_"));
        this.setProperty(Application.userName, headOut.getString("UserName_"));
        this.setProperty(Application.deviceLanguage, headOut.getString("Language_"));

        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionBase, token)) {
            buff.setField("LoginTime_", headOut.getDateTime("LoginTime_"));
            buff.setField("UserID_", headOut.getString("UserID_"));
            buff.setField("UserCode_", userCode);
            buff.setField("CorpNo_", corpNo);
            buff.setField("UserName_", headOut.getString("UserName_"));
            buff.setField("RoleCode_", headOut.getString("RoleCode_"));
            buff.setField("ProxyUsers_", headOut.getString("ProxyUsers_"));
            buff.setField("Language_", headOut.getString("Language_"));
            buff.setField("exists", true);
        }
        return true;
    }

    @Override
    public boolean init(String token) {
        this.setProperty(Application.token, token);
        log.debug(String.format("根据 token=%s 初始化 Session", token));
        if (token == null)
            return false;
        if (token.length() < 10)
            throw new RuntimeException("token 值有错！");

        // 从数据表CurrentUser中，取出公司别CorpNo_与UserCode_，再依据UserCode_从Account取出RoleCode_
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getSessionBase, token)) {
            if (buff.isNull()) {
                buff.setField("exists", false);
                LocalService svr = new LocalService(this, "AppSessionRestore.byToken");
                if (!svr.exec("token", token)) {
                    log.error("sid 恢复错误 ", svr.getMessage());
                    this.setProperty(Application.token, null);
                    return false;
                }
                Record headOut = svr.getDataOut().getHead();
                buff.setField("LoginTime_", headOut.getDateTime("LoginTime_"));
                buff.setField("UserID_", headOut.getString("UserID_"));
                buff.setField("UserCode_", headOut.getString("UserCode_"));
                buff.setField("CorpNo_", headOut.getString("CorpNo_"));
                buff.setField("UserName_", headOut.getString("UserName_"));
                buff.setField("RoleCode_", headOut.getString("RoleCode_"));
                buff.setField("ProxyUsers_", headOut.getString("ProxyUsers_"));
                buff.setField("Language_", headOut.getString("Language_"));
                buff.setField("exists", true);
            }
            if (buff.getBoolean("exists")) {
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

    private String GuidFixStr(String guid) {
        String str = guid.substring(1, guid.length() - 1);
        return str.replaceAll("-", "");
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
        if (corpNo == null || "".equals(corpNo)) {
            return false;
        }
        return true;
    }

    @Override
    public Object getProperty(String key) {
        if (key == null) {
            return this;
        }

        Object result = params.get(key);
        if (result == null && !params.containsKey(key)) {
            if (connections.containsKey(key))
                return connections.get(key);
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
            if ("{}".equals(value) || "".equals(key))
                params.put(key, null);
            else
                params.put(key, value);
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
                if (sess instanceof AutoCloseable)
                    ((AutoCloseable) sess).close();
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
