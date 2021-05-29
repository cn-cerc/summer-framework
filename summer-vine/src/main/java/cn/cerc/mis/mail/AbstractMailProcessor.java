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
        String body = message.getMessageBody();
        if (body == null)
            queue.deleteMessage(message.getReceiptHandle());

        Gson gson = new Gson();
        AbstractMailQueue record = gson.fromJson(body, AbstractMailQueue.class);

        MailSender sender = new MailSender();
        sender.setToAddress(record.getTo().getAddress());
        sender.setSubject(record.getSubject());
        sender.setContent(record.getContent());
        try {
            sender.send();
            queue.deleteMessage(message.getReceiptHandle());
        } catch (UnsupportedEncodingException | MessagingException | GeneralSecurityException e) {
            queue.deleteMessage(message.getReceiptHandle());
            log.error(e.getMessage(), e);
        }
    }

}