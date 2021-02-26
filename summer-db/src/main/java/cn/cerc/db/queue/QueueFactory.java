package cn.cerc.db.queue;

import cn.cerc.core.ClassResource;
import cn.cerc.db.core.ServerConfig;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;

public class QueueFactory {
    private static final ClassResource res = new ClassResource("summer-db", QueueFactory.class);

    private static MNSClient client;
    private static CloudAccount account;
    private static ServerConfig config;

    static {
        config = ServerConfig.getInstance();
        String server = config.getProperty(AliyunQueueConnection.AccountEndpoint, null);
        String userCode = config.getProperty(AliyunQueueConnection.AccessKeyId, null);
        String password = config.getProperty(AliyunQueueConnection.AccessKeySecret, null);
        String token = config.getProperty(AliyunQueueConnection.SecurityToken, "");
        if (server == null) {
            throw new RuntimeException(String.format(res.getString(1, "%s 配置为空"), AliyunQueueConnection.AccountEndpoint));
        }
        if (userCode == null) {
            throw new RuntimeException(String.format(res.getString(1, "%s 配置为空"), AliyunQueueConnection.AccessKeyId));
        }
        if (password == null) {
            throw new RuntimeException(String.format(res.getString(1, "%s 配置为空"), AliyunQueueConnection.AccessKeySecret));
        }
        if (token == null) {
            throw new RuntimeException(String.format(res.getString(1, "%s 配置为空"), AliyunQueueConnection.SecurityToken));
        }
        if (account == null) {
            account = new CloudAccount(userCode, password, server, token);
        }
        client = account.getMNSClient();
    }

    public static Queue getQueue(String propertKey) {
        String queueId = config.getProperty(propertKey);
        if (queueId == null) {
            throw new RuntimeException(String.format(res.getString(2, "application.properties 中没有找到配置项：%s"), propertKey));
        }

        CloudQueue queue = client.getQueueRef(queueId);
        return new Queue(queue);
    }
}
