package cn.cerc.db.queue;

import cn.cerc.core.Utils;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Queue {

    private CloudQueue client;
    private String receiptHandle;
    private Message message;

    public Queue(CloudQueue client) {
        this.client = client;
    }

    public String read() {
        message = client.popMessage();
        if (message != null) {
            log.debug("messageBody：{}" , message.getMessageBodyAsString());
            log.debug("messageId：{}" , message.getMessageId());
            log.debug("receiptHandle：{}" , message.getReceiptHandle());
            log.debug(message.getMessageBody());
            receiptHandle = message.getReceiptHandle();
            return message.getMessageBody();
        } else {
            return null;
        }
    }

    public Object readObject() {
        try {
            String str = this.read();
            if (str == null) {
                return null;
            }
            return Utils.deserializeToObject(str);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void append(String content) {
        message = new Message();
        message.setMessageBody(content);
        client.putMessage(message);
    }

    public void appendObject(Object obj) {
        try {
            this.append(Utils.serializeToString(obj));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        if (receiptHandle == null) {
            return;
        }
        client.deleteMessage(receiptHandle);
        receiptHandle = null;
        return;
    }

    public String getBodyText() {
        return message != null ? message.getMessageBody() : null;
    }

    public Message getMessage() {
        return message;
    }

}
