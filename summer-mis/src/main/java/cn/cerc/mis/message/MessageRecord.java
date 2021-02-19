package cn.cerc.mis.message;

import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.mis.core.LocalService;

/**
 * 专用于消息发送
 * 
 * 注意：公司别和用户代码必须配套
 */
public class MessageRecord {
    private String corpNo;
    private String userCode;
    private String subject;
    private StringBuilder content = new StringBuilder();
    private MessageLevel level = MessageLevel.General;
    private int process;

    public MessageRecord() {

    }

    public MessageRecord(String userCode) {
        this.userCode = userCode;
    }

    public MessageRecord(String userCode, String subject) {
        if (subject == null || "".equals(subject)) {
            throw new RuntimeException("消息标题不允许为空");
        }

        if (userCode == null || "".equals(userCode)) {
            throw new RuntimeException("用户代码不允许为空");
        }

        this.userCode = userCode;
        if (subject.length() > 80) {
            this.subject = subject.substring(0, 77) + "...";
            this.content.append(subject);
        } else {
            this.subject = subject;
        }
    }

    public String send(IHandle handle) {
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

        LocalService svr = new LocalService(handle, "SvrUserMessages.appendRecord");
        Record headIn = svr.getDataIn().getHead();
        headIn.setField("corpNo", sendCorpNo);
        headIn.setField("userCode", userCode);
        headIn.setField("level", level.ordinal());
        headIn.setField("subject", subject);
        headIn.setField("content", content.toString());
        headIn.setField("process", process);
        if (!svr.exec()) {
            throw new RuntimeException(svr.getMessage());
        }

        // 返回消息的编号
        return svr.getDataOut().getHead().getString("msgId");
    }

    public String getContent() {
        return content.toString();
    }

    public void append(String content) {
        this.content.append(content);
    }

    public void append(String format, Object... args) {
        content.append(String.format(format, args));
    }

    public MessageLevel getLevel() {
        return level;
    }

    public MessageRecord setLevel(MessageLevel level) {
        this.level = level;
        return this;
    }

    public String getUserCode() {
        return userCode;
    }

    public MessageRecord setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    public String getCorpNo() {
        return corpNo;
    }

    public MessageRecord setCorpNo(String corpNo) {
        this.corpNo = corpNo;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MessageRecord setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MessageRecord setSubject(String format, Object... args) {
        this.subject = String.format(format, args);
        return this;
    }

    public MessageRecord setContent(String content) {
        this.content = new StringBuilder(content);
        return this;
    }

    public int getProcess() {
        return process;
    }

    public MessageRecord setProcess(int process) {
        this.process = process;
        return this;
    }

}
