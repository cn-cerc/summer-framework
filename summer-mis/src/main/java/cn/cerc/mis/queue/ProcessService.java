package cn.cerc.mis.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.mis.client.AutoService;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IUserMessage;
import cn.cerc.mis.message.MessageProcess;
import cn.cerc.mis.task.AbstractTask;

/**
 * 处理后台异步任务
 *
 * @author ZhangGong
 */
public class ProcessService extends AbstractTask {
    private static final Logger log = LoggerFactory.getLogger(ProcessService.class);

    // 手动执行所有的预约服务
    public static void main(String[] args) {
        Application.initOnlyFramework();
        ISession session = Application.getSession();

        ProcessService ps = new ProcessService();
        ps.setSession(session);
        ps.run();
    }

    @Override
    public void execute() throws JsonProcessingException {
        IUserMessage um = Application.getBean(this, IUserMessage.class);
        for (String uid : um.getWaitList()) {
            log.info("开始处理异步任务，UID=" + uid);
            processService(uid);
        }
    }

    /**
     * 处理一个服务
     */
    private void processService(String taskId) throws JsonProcessingException {
        // 此任务可能被其它主机抢占
        IUserMessage um = Application.getBean(this, IUserMessage.class);
        Record ds = um.readAsyncService(taskId);
        if (ds == null) {
            return;
        }
        String corpNo = ds.getString("corpNo");
        String userCode = ds.getString("userCode");
        String content = ds.getString("content");
        String subject = ds.getString("subject");

        // 读取并标识为工作中，以防被其它用户抢占
        AsyncService async = new AsyncService();
        async.read(content);
        async.setProcess(MessageProcess.working);
        updateTaskprocess(async, taskId, subject);
        try {
            // 执行指定的数据服务
            AutoService auto = new AutoService(this, corpNo, userCode, async.getService());
            auto.getDataIn().appendDataSet(async.getDataIn(), true);
            if (auto.exec()) {
                async.getDataOut().appendDataSet(auto.getDataOut(), true);
                async.setProcess(MessageProcess.ok);
            } else {
                async.getDataOut().appendDataSet(auto.getDataOut(), true);
                async.setProcess(MessageProcess.error);
            }
            async.getDataOut().getHead().setField("_message_", auto.getMessage());
            updateTaskprocess(async, taskId, subject);
        } catch (Throwable e) {
            e.printStackTrace();
            async.setProcess(MessageProcess.error);
            async.getDataOut().getHead().setField("_message_", e.getMessage());
            updateTaskprocess(async, taskId, subject);
        }
    }

    /**
     * 更新队列的消息状态
     */
    private void updateTaskprocess(AsyncService async, String msgId, String subject) {
        async.setProcessTime(TDateTime.now().toString());
        IUserMessage um = Application.getBean(this, IUserMessage.class);
        if (!um.updateAsyncService(msgId, async.toString(), async.getProcess())) {
            throw new RuntimeException(String.format("msgId %s not find.", msgId));
        }

        log.debug(async.getService() + ":" + subject + ":" + async.getProcess().getTitle());
    }
}
