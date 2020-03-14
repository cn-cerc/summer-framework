package cn.cerc.mis.queue;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.TDateTime;
import cn.cerc.mis.client.AutoService;
import cn.cerc.mis.core.LocalService;
import cn.cerc.mis.message.MessageProcess;
import cn.cerc.mis.rds.StubHandle;
import cn.cerc.mis.task.AbstractTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理后台异步任务
 *
 * @author ZhangGong
 */
@Slf4j
public class ProcessService extends AbstractTask {

    // 手动执行所有的预约服务
    public static void main(String[] args) {
        StubHandle handle = new StubHandle();
        ProcessService ps = new ProcessService();
        ps.setHandle(handle);
        ps.run();
    }

    @Override
    public void execute() {
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
    private void processService(String msgId) {
        // 此任务可能被其它主机抢占
        LocalService svrMsg = new LocalService(this, "SvrUserMessages.readAsyncService");
        if (!svrMsg.exec("msgId", msgId)) {
            return;
        }
        Record ds = svrMsg.getDataOut().getHead();
        String corpNo = ds.getString("corpNo");
        String userCode = ds.getString("userCode");
        String content = ds.getString("content");
        String subject = ds.getString("subject");

        // 读取并标识为工作中，以防被其它用户抢占
        AsyncService svrSync = new AsyncService();
        svrSync.read(content);
        svrSync.setProcess(MessageProcess.working.ordinal());
        updateMessage(svrSync, msgId, subject);
        try {
            AutoService svrAuto = new AutoService(corpNo, userCode, svrSync.getService());
            svrAuto.getDataIn().appendDataSet(svrSync.getDataIn(), true);
            if (svrAuto.exec()) {
                svrSync.getDataOut().appendDataSet(svrAuto.getDataOut(), true);
                svrSync.setProcess(MessageProcess.ok.ordinal());
            } else {
                svrSync.getDataOut().appendDataSet(svrAuto.getDataOut(), true);
                svrSync.setProcess(MessageProcess.error.ordinal());
            }
            svrSync.getDataOut().getHead().setField("_message_", svrAuto.getMessage());
            updateMessage(svrSync, msgId, subject);
        } catch (Throwable e) {
            e.printStackTrace();
            svrSync.setProcess(MessageProcess.error.ordinal());
            svrSync.getDataOut().getHead().setField("_message_", e.getMessage());
            updateMessage(svrSync, msgId, subject);
        }
    }

    /**
     * 保存到数据库
     */
    private void updateMessage(AsyncService task, String msgId, String subject) {
        task.setProcessTime(TDateTime.Now().toString());
        LocalService svr = new LocalService(this, "SvrUserMessages.updateAsyncService");
        if (!svr.exec("msgId", msgId, "content", task.toString(), "process", task.getProcess())) {
            throw new RuntimeException("更新任务队列进度异常：" + svr.getMessage());
        }
        log.debug(task.getService() + ":" + subject + ":" + AsyncService.getProcessTitle(task.getProcess()));
    }
}
