package cn.cerc.mis.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.queue.QueueServer;
import cn.cerc.mis.core.SystemBuffer.SyncServer;

public class SyncServerQueue implements ISyncServer {

    private static final Logger log = LoggerFactory.getLogger(SyncServerQueue.class);

    private SyncServer pushFrom;
    private SyncServer pushTo;

    private SyncServer popFrom;
    private SyncServer popTo;

    public void initPushQueue(SyncServer pushFrom, SyncServer pushTo) {
        this.pushFrom = pushFrom;
        this.pushTo = pushTo;
    }

    public void initPopQueue(SyncServer popFrom, SyncServer popTo) {
        this.popFrom = popFrom;
        this.popTo = popTo;
    }

    @Override
    public void push(ISession session, Record record) {
        if (pushFrom == null)
            throw new RuntimeException("pushFrom is null");
        if (pushTo == null)
            throw new RuntimeException("pushTo is null");

        // 初始化客户端
        QueueServer mns = (QueueServer) session.getProperty(QueueServer.SessionId);

        // 数据写入队列
        String queueCode = pushFrom.name().toLowerCase() + "-to-" + pushTo.name().toLowerCase();
        CloudQueue queue = mns.openQueue(queueCode);

        Message message = new Message();
        message.setMessageBody(record.toString());
        queue.putMessage(message);
    }

    @Override
    public void repush(ISession session, Record record) {
        throw new RuntimeException("this is repush disabled.");
    }

    @Override
    public int pop(ISession session, IPopProcesser popProcesser, int maxRecords) {
        if (popFrom == null)
            throw new RuntimeException("popFrom is null");
        if (popTo == null)
            throw new RuntimeException("popTo is null");

        // 取出数据队列
        String queueCode = popFrom.name().toLowerCase() + "-to-" + popTo.name().toLowerCase();
        QueueServer mns = (QueueServer) session.getProperty(QueueServer.SessionId);
        CloudQueue queue = mns.openQueue(queueCode);

        for (int i = 0; i < maxRecords; i++) {
            Message msg = queue.popMessage();
            if (msg == null) {
                return i;
            }
            String receiptHandle = msg.getReceiptHandle();
            String body = msg.getMessageBody();
            if (body == null) {
                queue.deleteMessage(receiptHandle);
                continue;
            }

            Record record = new Record();
            record.setJSON(body);
            try {
                if (!popProcesser.popRecord(session, record, true)) {
                    log.error("{} 处理失败，请检查数据源和帐套信息 {}", receiptHandle, body);
                }
                queue.deleteMessage(receiptHandle);
            } catch (Exception e) {
                log.error(record.toString());
                e.printStackTrace();
            }
        }
        return maxRecords;
    }

}
