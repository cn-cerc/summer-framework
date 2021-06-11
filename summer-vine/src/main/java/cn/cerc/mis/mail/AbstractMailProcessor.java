package cn.cerc.mis.mail;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.google.gson.Gson;

import cn.cerc.db.queue.QueueServer;

public abstract class AbstractMailProcessor {
    private static final Logger log = LoggerFactory.getLogger(AbstractMailProcessor.class);
    private static final QueueServer mns = new QueueServer();

    public void execute(String queueCode) {
        CloudQueue queue = mns.openQueue(queueCode);
        Message message = queue.popMessage();
        if (message == null) {
            return;
        }

        while (message != null) {
            String body = message.getMessageBody();
            if (body == null)
                queue.deleteMessage(message.getReceiptHandle());

            Gson gson = new Gson();
            AbstractMailQueue record = gson.fromJson(body, AbstractMailQueue.class);

            AliyunMailClient client = new AliyunMailClient();
            client.setTo(record.getTo().getAddress());
            client.setSubject(record.getSubject());
            client.setContent(record.getContent());
            try {
                client.send();
                queue.deleteMessage(message.getReceiptHandle());
            } catch (UnsupportedEncodingException | MessagingException | GeneralSecurityException e) {
                queue.deleteMessage(message.getReceiptHandle());
                log.error(e.getMessage(), e);
            }
            message = queue.popMessage();
        }
    }

}