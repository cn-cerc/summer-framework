package cn.cerc.db.queue;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;

import cn.cerc.core.Utils;

public class Queue {
    private static final Logger log = LoggerFactory.getLogger(Queue.class);

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
