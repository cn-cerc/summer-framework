package cn.cerc.db.jiguang;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;
import cn.cerc.db.core.ServerConfig;
import cn.jpush.api.JPushClient;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JiguangConnection implements IConnection {
    // IHandle中识别码
    public static final String sessionId = "jiguangSession";
    // 配置文件
    public static final String masterSecret = "jiguang.masterSecret";
    public static final String appKey = "jiguang.appKey";
    private static JPushClient client = null;
    private IConfig config;

    public JiguangConnection() {
        config = ServerConfig.getInstance();
    }

    @Override
    public JPushClient getClient() {
        if (client != null)
            return client;

        String masterSecret = config.getProperty(JiguangConnection.masterSecret);
        if (masterSecret == null)
            throw new RuntimeException("jiguang.masterSecret is null");

        String appKey = config.getProperty(JiguangConnection.appKey);
        if (appKey == null)
            throw new RuntimeException("jiguang.appKey is null");

        client = new JPushClient(masterSecret, appKey);

        return client;
    }

    @Override
    public String getClientId() {
        return sessionId;
    }

    public IConfig getConfig() {
        return config;
    }

    @Override
    public void setConfig(IConfig config) {
        this.config = config;
    }

}
