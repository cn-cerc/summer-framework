package cn.cerc.mis.message;

import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.queue.QueueDB;
import cn.cerc.db.queue.QueueMode;
import cn.cerc.db.queue.QueueQuery;

/**
 * 消息发送队列
 * 
 * 注意：公司别和用户代码必须配套
 */
public class MessageQueue {
    private String corpNo;
    private String userCode;
    private String subject;
    private StringBuilder content = new StringBuilder();
    private MessageLevel level = MessageLevel.General;
    private int process;
    // 极光推送消息提示音
    private String sound;

    public MessageQueue() {
    }

    public MessageQueue(String userCode) {
        this.userCode = userCode;
    }

    public MessageQueue(String userCode, String subject) {
        this.userCode = userCode;

        if (subject.length() > 80) {
            this.subject = subject.substring(0, 77) + "...";
            this.content.append(subject);
        } else {
            this.subject = subject;
        }
    }

    public void send(IHandle handle) {
        if (subject == null || "".equals(subject)) {
            throw new RuntimeException("消息标题不允许为空");
        }

        if (userCode == null || "".equals(userCode)) {
            throw new RuntimeException("用户代码不允许为空");
        }

        String sendCorpNo = corpNo != null ? corpNo : handle.getCorpNo();
        if ("".equals(sendCorpNo)) {
            throw new RuntimeException("公司别不允许为空");
        }

        // 将消息发送至阿里云MNS
        QueueQuery query = new QueueQuery(handle);
        query.setQueueMode(QueueMode.append);
        if (ServerConfig.isServerDevelop()) {
            query.add("select * from %s", QueueDB.MESSAGE_TEST);
        } else {
            query.add("select * from %s", QueueDB.MESSAGE);
        }
        query.open();

        Record headIn = query.getHead();
        headIn.setField("CorpNo_", sendCorpNo);
        headIn.setField("UserCode_", userCode);
        headIn.setField("Level_", level.ordinal());
        headIn.setField("Process_", process);
        headIn.setField("Subject_", subject);
        headIn.setField("Content_", content.toString());
        headIn.setField("Sound_", sound);
        query.save();
    }

    public String getContent() {
        return content.toString();
    }

    public void append(Object obj) {
        content.append(obj);
    }

    public void append(String format, Object... args) {
        content.append(String.format(format, args));
    }

    public MessageLevel getLevel() {
        return level;
    }

    public MessageQueue setLevel(MessageLevel level) {
        this.level = level;
        return this;
    }

    public String getUserCode() {
        return userCode;
    }

    public MessageQueue setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    public String getCorpNo() {
        return corpNo;
    }

    public MessageQueue setCorpNo(String corpNo) {
        this.corpNo = corpNo;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MessageQueue setSubject(String format, Object... args) {
        this.subject = String.format(format, args);
        return this;
    }

    public MessageQueue setContent(String content) {
        this.content = new StringBuilder(content);
        return this;
    }

    public int getProcess() {
        return process;
    }

    public MessageQueue setProcess(int process) {
        this.process = process;
        return this;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

}
