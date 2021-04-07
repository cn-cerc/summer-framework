package cn.cerc.mis.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.client.AutoService;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.Handle;
import cn.cerc.mis.core.LocalService;
import cn.cerc.mis.message.MessageProcess;
import cn.cerc.mis.task.AbstractTask;

/**
 * 处理后台异步任务
 *
 * @author ZhangGong
 */
public class ProcessService extends AbstractTask {
    private static final Logger log = LoggerFactory.getLogger(ProcessService.class);
    private static final ClassResource res = new ClassResource(ProcessService.class, SummerMIS.ID);

    // 手动执行所有的预约服务
    public static void main(String[] args) {
        Application.init(SummerMIS.ID);
        ISession session = Application.createSession();
        IHandle handle = new Handle(session);

        ProcessService ps = new ProcessService();
        ps.setHandle(handle);
        ps.run();
    }

    @Override
    public void execute() throws JsonProcessingException {
        // FIXME 此处应该做进一步抽象
        LocalService svr = new LocalService(this, "SvrUserMessages.getWaitList");
        if (!svr.exec()) {
            throw new RuntimeException(svr.getMessage());
        }
        DataSet ds = svr.getDataOut();
        while (ds.fetch()) {
            log.info("开始处理异步任务，UID=" + ds.getString("UID_"));
            processService(ds.getString("UID_"));
        }
    }

    /**
     * 处理一个服务
     */
    private void processService(String taskId) throws JsonProcessingException {
        // 此任务可能被其它主机抢占
        // FIXME 此处应该做进一步抽象
        LocalService svrMsg = new LocalService(this, "SvrUserMessages.readAsyncService");
        if (!svrMsg.exec("msgId", taskId)) {
            return;
        }
        Record ds = svrMsg.getDataOut().getHead();
        String corpNo = ds.getString("corpNo");
        String userCode = ds.getString("userCode");
        String content = ds.getString("content");
        String subject = ds.getString("subject");

        // 读取并标识为工作中，以防被其它用户抢占
        AsyncService async = new AsyncService();
        async.read(content);
        async.setProcess(MessageProcess.working.ordinal());
        updateTaskprocess(async, taskId, subject);
        try {
            // 执行指定的数据服务
            AutoService auto = new AutoService(this.handle, corpNo, userCode, async.getService());
            auto.getDataIn().appendDataSet(async.getDataIn(), true);
            if (auto.exec()) {
                async.getDataOut().appendDataSet(auto.getDataOut(), true);
                async.setProcess(MessageProcess.ok.ordinal());
            } else {
                async.getDataOut().appendDataSet(auto.getDataOut(), true);
                async.setProcess(MessageProcess.error.ordinal());
            }
            async.getDataOut().getHead().setField("_message_", auto.getMessage());
            updateTaskprocess(async, taskId, subject);
        } catch (Throwable e) {
            e.printStackTrace();
            async.setProcess(MessageProcess.error.ordinal());
            async.getDataOut().getHead().setField("_message_", e.getMessage());
            updateTaskprocess(async, taskId, subject);
        }
    }

    /**
     * 更新队列的消息状态
     */
    private void updateTaskprocess(AsyncService async, String taskId, String subject) {
        async.setProcessTime(TDateTime.now().toString());
        // FIXME 此处应该做进一步抽象
        LocalService svr = new LocalService(this, "SvrUserMessages.updateAsyncService");
        if (!svr.exec("msgId", taskId, "content", async.toString(), "process", async.getProcess())) {
            throw new RuntimeException(String.format(res.getString(1, "更新任务队列进度异常：%s"), svr.getMessage()));
        }
        log.debug(async.getService() + ":" + subject + ":" + AsyncService.getProcessTitle(async.getProcess()));
    }
}
