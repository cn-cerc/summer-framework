package cn.cerc.mis.queue;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.queue.QueueDB;
import cn.cerc.db.queue.QueueMode;
import cn.cerc.db.queue.QueueQuery;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.message.MessageLevel;
import cn.cerc.mis.message.MessageProcess;
import cn.cerc.mis.message.MessageRecord;

public class AsyncService implements IServiceProxy {
    private static final Logger log = LoggerFactory.getLogger(AsyncService.class);
    private static final ClassResource res = new ClassResource(AsyncService.class, SummerMIS.ID);

    // 状态列表
    private static List<String> processTiles = new ArrayList<>();

    static {
        processTiles.add(res.getString(1, "中止执行"));
        processTiles.add(res.getString(2, "排队中"));
        processTiles.add(res.getString(3, "正在执行中"));
        processTiles.add(res.getString(4, "执行成功"));
        processTiles.add(res.getString(5, "执行失败"));
    }

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

    public AsyncService() {

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

    public static String getProcessTitle(int process) {
        return processTiles.get(process);
    }

    public AsyncService read(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonString);
        this.setService(json.get("service").asText());
        if (json.has("dataOut")) {
            this.getDataOut().setJSON(json.get("dataOut").asText());
        }
        if (json.has("dataIn")) {
            this.getDataIn().setJSON(json.get("dataIn").asText());
        }
        if (json.has("process")) {
            this.setProcess(json.get("process").asInt());
        }
        if (json.has("timer")) {
            this.setTimer(json.get("timer").asText());
        }
        if (json.has("processTime")) {
            this.setProcessTime(json.get("processTime").asText());
        }
        return this;
    }

    @Override
    public boolean exec(Object... args) {
        Record headIn = getDataIn().getHead();
        if (args.length > 0) {
            if (args.length % 2 != 0) {
                throw new RuntimeException(res.getString(6, "传入的参数数量必须为偶数！"));
            }
            for (int i = 0; i < args.length; i = i + 2) {
                headIn.setField(args[i].toString(), args[i + 1]);
            }
        }
        headIn.setField("token", Application.getToken(handle));

        String subject = this.getSubject();
        if ("".equals(subject)) {
            throw new RuntimeException(res.getString(7, "后台任务标题不允许为空！"));
        }
        this.send(); // 发送到队列服务器

        getDataOut().getHead().setField("_msgId_", msgId);
        if (this.process == MessageProcess.working.ordinal()) {
            // 返回消息的编号插入到阿里云消息队列
            QueueQuery ds = new QueueQuery(handle);
            ds.setQueueMode(QueueMode.append);
            ds.add("select * from %s", QueueDB.SUMMER);
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
        if (handle == null) {
            throw new RuntimeException("handle is null");
        }
        String subject = this.getSubject();
        if (subject == null || "".equals(subject)) {
            throw new RuntimeException("subject is null");
        }
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
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode content = mapper.createObjectNode();

        content.put("service", this.service);
        if (this.dataIn != null) {
            content.put("dataIn", dataIn.getJSON());
        }
        if (this.dataOut != null) {
            content.put("dataOut", dataOut.getJSON());
        }
        content.put("timer", this.timer);
        content.put("process", this.process);
        if (this.processTime != null) {
            content.put("processTime", this.processTime);
        }
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
        if (dataIn == null) {
            dataIn = new DataSet();
        }
        return dataIn;
    }

    public void setDataIn(DataSet dataIn) {
        this.dataIn = dataIn;
    }

    @Override
    public DataSet getDataOut() {
        if (dataOut == null) {
            dataOut = new DataSet();
        }
        return dataOut;
    }

    public void setDataOut(DataSet dataOut) {
        this.dataOut = dataOut;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        if (process < 0 || process > processTiles.size()) {
            throw new RuntimeException(String.format(res.getString(8, "非法的任务进度值：%s"), process));
        }
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
        if (dataOut == null) {
            return null;
        }
        if (!dataOut.getHead().exists(_message_)) {
            return null;
        }
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
