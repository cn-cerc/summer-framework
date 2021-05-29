package cn.cerc.mis.mail;

import com.aliyun.mns.client.CloudQueue;
import com.google.gson.Gson;

import cn.cerc.db.queue.QueueServer;

public class AbstractMailQueue {
    private final Address to = new Address();
    private String subject;
    private String content;

    public boolean send(String queueCode) {
        QueueServer sess = new QueueServer();
        CloudQueue queue = sess.openQueue(queueCode);
        return sess.append(queue, this.toString());
    }

    public AbstractMailQueue(String to, String subject, String content) {
        this.to.setAddress(to);
        this.subject = subject;
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Address getTo() {
        return to;
    }

    @Override
    public String toString() {
        return (new Gson()).toJson(this);
    }

}
