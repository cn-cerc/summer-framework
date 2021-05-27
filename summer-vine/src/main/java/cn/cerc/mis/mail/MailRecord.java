package cn.cerc.mis.mail;

import cn.cerc.db.core.Handle;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.queue.QueueServer;

import com.aliyun.mns.client.CloudQueue;
import com.google.gson.Gson;

import java.io.Serializable;

public class MailRecord extends Handle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String to_addr;
    private String to_name;
    private String subject;
    private String content;

    public MailRecord(IHandle handle) {
        super(handle);
    }

    public boolean send() {
        QueueServer sess = (QueueServer) this.getSession().getProperty(QueueServer.SessionId);
        CloudQueue queue = sess.openQueue(AppMailQueue.queueSendMail);
        return sess.append(queue, this.toString());
    }

    public MailRecord(String to, String subject, String content) {
        this.to_addr = to;
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

    public String getTo_addr() {
        return to_addr;
    }

    public void setTo_addr(String to_addr) {
        this.to_addr = to_addr;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
