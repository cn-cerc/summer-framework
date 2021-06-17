package cn.cerc.db.jiguang;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.PushPayload.Builder;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class JiguangPush {
    private static final Logger log = LoggerFactory.getLogger(JiguangPush.class);

    private JiguangConnection connection;
    // 消息id，回调时使用
    private String msgId;
    // 消息标题，仅安卓机型有效，IOS设备忽略，默认为应用标题
    private String title;
    // 消息内容
    private String message;
    // 附加参数
    private Map<String, String> params = new LinkedHashMap<>();

    public JiguangPush() {
    }

    public JiguangPush(ISession session) {
        this.connection = (JiguangConnection) session.getProperty(JiguangConnection.sessionId);
    }

    public JiguangPush(IHandle owner) {
        this(owner.getSession());
    }

    public void send(ClientType clientType, String clientId) {
        this.send(clientType, clientId, "default");
    }

    /**
     * 发送给指定设备
     *
     * @param clientType 设备类型
     * @param clientId   设备id
     * @param sound      声音类型
     */
    public void send(ClientType clientType, String clientId, String sound) {
        if (msgId == null) {
            throw new RuntimeException("msgId is null");
        }
        addParam("msgId", msgId);
        addParam("sound", sound);

        Builder builder = PushPayload.newBuilder();

        // 发送给指定的设备
        if (clientId != null) {
            builder.setAudience(Audience.alias(clientId));
            builder.setPlatform(Platform.android_ios());
        } else {
            builder.setAudience(Audience.all());
        }

        if (clientType != ClientType.Android && clientType != ClientType.IOS) {
            throw new RuntimeException("unsupport device type ：" + clientType.ordinal());
        }

        builder.setNotification(Notification.newBuilder().setAlert(message)
                .addPlatformNotification(AndroidNotification.newBuilder().setTitle(this.title).addExtras(params).build())
                .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtras(params).setSound(sound).build())
                .build()
        ).build();
        // 设置生产环境
        builder.setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
        PushPayload payload = builder.build();
        try {
            PushResult result = connection.getClient().sendPush(payload);
            log.info("Got result - " + result);
        } catch (APIConnectionException e) {
            log.error("Connection error, should retry later", e);
        } catch (APIRequestException e) {
            log.error("Should review the error, and fix the request", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("PushPayload Message: " + payload);
        }
    }

    /**
     * /** 增加附加参数到 extras
     *
     * @param key   增加附加参数到 extras
     * @param value 无返回值
     */
    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public String getMsgId() {
        return msgId;
    }

    public JiguangPush setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JiguangPush setTitle(String title) {
        this.title = title;
        return this;
    }

    public JiguangPush setTitle(String format, Object... args) {
        this.title = String.format(format, args);
        return this;
    }

    public JiguangConnection getConnection() {
        return connection;
    }

    public void setConnection(JiguangConnection connection) {
        this.connection = connection;
    }

    public String getMessage() {
        return message;
    }

    public JiguangPush setMessage(String message) {
        this.message = message;
        return this;
    }

}
