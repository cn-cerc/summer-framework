package cn.cerc.mis.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ITokenManage;
import cn.cerc.db.redis.JedisFactory;
import cn.cerc.mis.config.ApplicationConfig;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.CenterService;
import cn.cerc.mis.core.Handle;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;
import redis.clients.jedis.Jedis;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TokenManageDefault implements ITokenManage {
    private static final Logger log = LoggerFactory.getLogger(TokenManageDefault.class);
    private ISession session;

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    /**
     * 根据token恢复用户session
     */
    @Override
    public boolean resumeToken(String token) {
        session.setProperty(Application.TOKEN, token);
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

        try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.Token.SessionBase, token);
                Jedis redis = JedisFactory.getJedis()) {
            if (buff.isNull()) {
                buff.setField("exists", false);
                CenterService svr = new CenterService(new Handle(session));
                svr.setService("SvrSession.byToken");
                if (!svr.exec("token", token)) {
                    log.error("token restore error，{}", svr.getMessage());
                    session.setProperty(Application.TOKEN, null);
                    return false;
                }
                Record record = svr.getDataOut().getHead();
                log.debug(record.toString());

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
                session.setProperty(Application.loginTime, buff.getDateTime("LoginTime_"));
                session.setProperty(Application.bookNo, buff.getString("CorpNo_"));
                session.setProperty(Application.userId, buff.getString("UserID_"));
                session.setProperty(Application.userCode, buff.getString("UserCode_"));
                session.setProperty(Application.userName, buff.getString("UserName_"));
                session.setProperty(Application.ProxyUsers, buff.getString("ProxyUsers_"));
                session.setProperty(Application.roleCode, buff.getString("RoleCode_"));
                session.setProperty(Application.deviceLanguage, buff.getString("Language_"));

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
        IHandle handle = new Handle(session);
        String token = ApplicationConfig.getAuthToken(userCode, password, machineCode, handle);
        if (Utils.isEmpty(token)) {
            return false;
        }
        session.setProperty(Application.TOKEN, token);
        session.setProperty(Application.bookNo, corpNo);
        session.setProperty(Application.userCode, userCode);
        session.setProperty(Application.clientIP, "0.0.0.0");

        // 将用户信息赋值到句柄
        CenterService svr = new CenterService(handle);
        svr.setService("SvrSession.byUserCode");
        if (!svr.exec("CorpNo_", corpNo, "UserCode_", userCode)) {
            throw new RuntimeException(svr.getMessage());
        }
        Record record = svr.getDataOut().getHead();
        session.setProperty(Application.userId, record.getString("UserID_"));
        session.setProperty(Application.loginTime, record.getDateTime("LoginTime_"));
        session.setProperty(Application.roleCode, record.getString("RoleCode_"));
        session.setProperty(Application.ProxyUsers, record.getString("ProxyUsers_"));
        session.setProperty(Application.userName, record.getString("UserName_"));
        session.setProperty(Application.deviceLanguage, record.getString("Language_"));

        // 将用户信息赋值到缓存
        try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.Token.SessionBase, token)) {
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

}
