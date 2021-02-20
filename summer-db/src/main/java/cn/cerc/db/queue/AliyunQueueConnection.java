package cn.cerc.db.queue;

import cn.cerc.core.IConfig;
import cn.cerc.core.IConnection;
import cn.cerc.db.core.ServerConfig;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.QueueMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AliyunQueueConnection implements IConnection {

    public static final String AccountEndpoint = "mns.accountendpoint";
    public static final String AccessKeyId = "mns.accesskeyid";
    public static final String AccessKeySecret = "mns.accesskeysecret";
    public static final String SecurityToken = "mns.securitytoken";
    // IHandle中识别码
    public static final String sessionId = "aliyunQueueSession";

    // 默认不可见时间
    private static int visibilityTimeout = 50;
    private static MNSClient client;
    private static CloudAccount account;
    private IConfig config;

    public AliyunQueueConnection() {
        config = ServerConfig.getInstance();
    }

    @Override
    public MNSClient getClient() {
        if (client != null) {
            return client;
        }

        if (account == null) {
            String server = config.getProperty(AliyunQueueConnection.AccountEndpoint, null);
            String userCode = config.getProperty(AliyunQueueConnection.AccessKeyId, null);
            String password = config.getProperty(AliyunQueueConnection.AccessKeySecret, null);
            if (server == null) {
                throw new RuntimeException(AliyunQueueConnection.AccountEndpoint + " 配置为空");
            }
            if (userCode == null) {
                throw new RuntimeException(AliyunQueueConnection.AccessKeyId + " 配置为空");
            }
            if (password == null) {
                throw new RuntimeException(AliyunQueueConnection.AccessKeySecret + " 配置为空");
            }
            if (account == null) {
                account = new CloudAccount(userCode, password, server);
            }
        }

        if (client == null) {
            client = account.getMNSClient();
        }

        return client;
    }

    /**
     * 根据队列的URL创建CloudQueue对象，后于后续对改对象的创建、查询等
     *
     * @param queueCode 队列代码
     * @return value 返回具体的消息队列
     */
    public CloudQueue openQueue(String queueCode) {
        return getClient().getQueueRef(queueCode);
    }

    /**
     * 创建队列
     *
     * @param queueCode 队列代码
     * @return value 返回创建的队列
     */
    public CloudQueue createQueue(String queueCode) {
        QueueMeta meta = new QueueMeta();
        // 设置队列的名字
        meta.setQueueName(queueCode);
        // 设置队列消息的长轮询等待时间，0为关闭长轮询
        meta.setPollingWaitSeconds(0);
        // 设置队列消息的最大长度，单位是byte
        meta.setMaxMessageSize(65356L);
        // 设置队列消息的最大长度，单位是byte
        meta.setMessageRetentionPeriod(72000L);
        // 设置队列消息的不可见时间，即取出消息隐藏时长，单位是秒
        meta.setVisibilityTimeout(180L);
        return getClient().createQueue(meta);
    }

    /**
     * 发送消息
     *
     * @param queue   消息队列
     * @param content 消息内容
     * @return value 返回值，当前均为true
     */
    public boolean append(CloudQueue queue, String content) {
        Message message = new Message();
        message.setMessageBody(content);
        queue.putMessage(message);
        return true;
    }

    /**
     * 获取队列中的消息
     *
     * @param queue 消息队列
     * @return value 返回请求的删除，可为null
     */
    public Message receive(CloudQueue queue) {
        Message message = null;
        try {
            message = queue.popMessage();
            if (message != null) {
                log.debug("消息内容：" + message.getMessageBodyAsString());
                log.debug("消息编号：" + message.getMessageId());
                log.debug("访问代码：" + message.getReceiptHandle());
            } else {
                log.debug("msg is null");
            }
        } catch (ServiceException | ClientException e) {
            log.debug(e.getMessage());
        }
        return message;
    }

    /**
     * 删除消息
     *
     * @param queue         队列
     * @param receiptHandle 消息句柄
     */
    public void delete(CloudQueue queue, String receiptHandle) {
        queue.deleteMessage(receiptHandle);
    }

    /**
     * 查看队列消息
     *
     * @param queue 队列
     * @return value 返回取得的消息体
     */
    public Message peek(CloudQueue queue) {
        return queue.peekMessage();
    }

    /**
     * 延长消息不可见时间
     *
     * @param queue         队列
     * @param receiptHandle 消息句柄
     */
    public void changeVisibility(CloudQueue queue, String receiptHandle) {
        // 第一个参数为旧的ReceiptHandle值，第二个参数为新的不可见时间（VisibilityTimeout）
        String newReceiptHandle = queue.changeMessageVisibilityTimeout(receiptHandle, visibilityTimeout);
        log.debug("新的消息句柄: " + newReceiptHandle);
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
