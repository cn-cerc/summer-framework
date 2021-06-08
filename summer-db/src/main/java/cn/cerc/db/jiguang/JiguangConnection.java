package cn.cerc.db.jiguang;

import org.springframework.stereotype.Component;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;
import cn.cerc.db.core.ServerConfig;
import cn.jpush.api.JPushClient;

@Component
public class JiguangConnection implements IConnection {
    // IHandle中识别码
    public static final String sessionId = "jiguangSession";

    // 配置文件
    public static final String masterSecret = "jiguang.masterSecret";
    public static final String appKey = "jiguang.appKey";
    private static JPushClient client;

    @Override
    public JPushClient getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    IConfig config = ServerConfig.getInstance();
                    String masterSecret = config.getProperty(JiguangConnection.masterSecret);
                    if (masterSecret == null) {
                        throw new RuntimeException("jiguang.masterSecret is null");
                    }
                    String appKey = config.getProperty(JiguangConnection.appKey);
                    if (appKey == null) {
                        throw new RuntimeException("jiguang.appKey is null");
                    }
                    client = new JPushClient(masterSecret, appKey);
                }
            }
        }
        return client;
    }

}
