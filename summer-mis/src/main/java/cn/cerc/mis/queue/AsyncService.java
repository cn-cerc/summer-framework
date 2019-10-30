package cn.cerc.mis.queue;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.queue.AliyunQueueConnection;
import cn.cerc.db.queue.QueueDB;
import cn.cerc.db.queue.QueueQuery;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.message.MessageLevel;
import cn.cerc.mis.message.MessageRecord;
import net.sf.json.JSONObject;

public class AsyncService implements IServiceProxy {
    private static final Logger log = LoggerFactory.getLogger(AsyncService.class);
    // 状态列表
    private static List<String> processTiles = new ArrayList<>();
    private String corpNo;
    private String userCode;
    // 预约的服务
    private String service;
    // 调用参数
    private DataSet dataIn;
    // 执行结果
    private DataSet dataOut;
    // 预约时间，若为空则表示立即执行
    private String timer;
    // 执行进度
    private int process = 1;
    // 处理时间
    private String processTime;
    //
    private IHandle handle;
    //
    private MessageLevel messageLevel = MessageLevel.Service;
    //
    private String msgId;

    static {
        processTiles.add("中止执行");
        processTiles.add("排队中");
        processTiles.add("正在执行中");
        processTiles.add("执行成功");
        processTiles.add("执行失败");
    }

    public AsyncService(IHandle handle) {
        this.handle = handle;
        if (handle != null) {
            this.setCorpNo(handle.getCorpNo());
            this.setUserCode(handle.getUserCode());
        }
    }

    public AsyncService(IHandle handle, String service) {
        this(handle);
        this.setService(service);
    }

    public AsyncService read(String jsonString) {
        JSONObject json = JSONObject.fromObject(jsonString);
        this.setService(json.getString("service"));
        if (json.containsKey("dataOut"))
            this.getDataOut().setJSON(json.getString("dataOut"));
        if (json.containsKey("dataIn"))
            this.getDataIn().setJSON(json.getString("dataIn"));
        if (json.containsKey("process"))
            this.setProcess(json.getInt("process"));
        if (json.containsKey("timer"))
            this.setTimer(json.getString("timer"));
        if (json.containsKey("processTime"))
            this.setProcessTime(json.getString("processTime"));
        return this;
    }

    @Override
    public boolean exec(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0)
                throw new RuntimeException("传入的参数数量必须为偶数！");
            for (int i = 0; i < args.length; i = i + 2)
                headIn.setField(args[i].toString(), args[i + 1]);
        }

        String subject = this.getSubject();
        if ("".equals(subject))
            throw new RuntimeException("后台任务标题不允许为空！");
        this.send(); // 发送到队列服务器
        getDataOut().getHead().setField("_msgId_", msgId);
        if (this.process == 2) {
            // 返回消息的编号插入到阿里云消息队列
            QueueQuery ds = new QueueQuery(handle);
            if (ServerConfig.isServerDevelop()) {
                ds.add("select * from %s", QueueDB.TEST);
            } else {
                ds.add("select * from %s", AliyunQueueConnection.defaultQueue);
            }
            ds.open();
            ds.appendDataSet(this.getDataIn(), true);
            ds.getHead().setField("_queueId_", msgId);
            ds.getHead().setField("_service_", this.service);
            ds.getHead().setField("_corpNo_", this.corpNo);
            ds.getHead().setField("_userCode_", this.userCode);
            ds.getHead().setField("_content_", this.toString());
            ds.save();
        }
        return !"".equals(msgId);
    }

    private void send() {
        if (handle == null)
            throw new RuntimeException("handle is null");
        String subject = this.getSubject();
        if (subject == null || "".equals(subject))
            throw new RuntimeException("subject is null");
        MessageRecord msg = new MessageRecord();
        msg.setCorpNo(this.getCorpNo());
        msg.setUserCode(this.getUserCode());
        msg.setLevel(this.messageLevel);
        msg.setContent(this.toString());
        msg.setSubject(subject);
        msg.setProcess(this.process);
        log.debug(this.getCorpNo() + ":" + this.getUserCode() + ":" + this);
        this.msgId = msg.send(handle);
    }

    @Override
    public String toString() {
        JSONObject content = new JSONObject();
        content.element("service", this.service);
        if (this.dataIn != null)
            content.element("dataIn", dataIn.getJSON());
        if (this.dataOut != null)
            content.element("dataOut", dataOut.getJSON());
        content.element("timer", this.timer);
        content.element("process", this.process);
        if (this.processTime != null)
            content.element("processTime", this.processTime);
        return content.toString();
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public AsyncService setService(String service) {
        this.service = service;
        return this;
    }

    @Override
    public DataSet getDataIn() {
        if (dataIn == null)
            dataIn = new DataSet();
        return dataIn;
    }

    public void setDataIn(DataSet dataIn) {
        this.dataIn = dataIn;
    }

    @Override
    public DataSet getDataOut() {
        if (dataOut == null)
            dataOut = new DataSet();
        return dataOut;
    }

    public void setDataOut(DataSet dataOut) {
        this.dataOut = dataOut;
    }

    public int getProcess() {
        return process;
    }

    public static String getProcessTitle(int process) {
        return processTiles.get(process);
    }

    public void setProcess(int process) {
        if (process < 0 || process > processTiles.size())
            throw new RuntimeException("非法的任务进度值：" + process);
        this.process = process;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public String getCorpNo() {
        return corpNo;
    }

    public void setCorpNo(String corpNo) {
        this.corpNo = corpNo;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public String getMessage() {
        if (dataOut == null)
            return null;
        if (!dataOut.getHead().exists(_message_))
            return null;
        return dataOut.getHead().getString(_message_);
    }

    public MessageLevel getMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(MessageLevel messageLevel) {
        this.messageLevel = messageLevel;
    }

    public String getSubject() {
        return getDataIn().getHead().getString("_subject_");
    }

    public void setSubject(String subject) {
        getDataIn().getHead().setField("_subject_", subject);
    }

    public void setSubject(String format, Object... args) {
        getDataIn().getHead().setField("_subject_", String.format(format, args));
    }

    public String getMsgId() {
        return msgId;
    }
}
