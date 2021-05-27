package cn.cerc.mis.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.mis.core.Application;

public class SyncDatabase implements IPopProcesser {
    private static final Logger log = LoggerFactory.getLogger(SyncDatabase.class);
    private ISyncServer queue;

    public SyncDatabase(ISyncServer queue) {
        super();
        this.queue = queue;
    }

    public void push(ISession session, String tableCode, Record record, SyncOpera opera) {
        Record rs = new Record();
        rs.setField("__table", tableCode);
        rs.setField("__opera", opera.ordinal());
        rs.copyValues(record);
        queue.push(session, rs);
    }

    public int pop(ISession session, int maxRecords) {
        return queue.pop(session, this, maxRecords);
    }

    @Override
    public boolean popRecord(ISession session, Record record, boolean isQueue) {
        String tableCode = record.getString("__table");
        int opera = record.getInt("__opera");
        int error = record.getInt("__error");
        record.delete("__table");
        record.delete("__opera");
        record.delete("__error");

        IPushProcesser processer = (IPushProcesser) Application.getBean(session, "sync_" + tableCode);
        if (processer == null) {
            processer = new PushTableDefault().setTableCode(tableCode);
        }
        processer.setSession(session);

        boolean result = false;
        try {
            switch (SyncOpera.values()[opera]) {
            case Append:
                result = processer.appendRecord(record);
                break;
            case Delete:
                result = processer.deleteRecord(record);
                break;
            case Update:
                result = processer.updateRecord(record);
                break;
            case Reset:
                result = processer.resetRecord(record);
                break;
            default:
                throw new RuntimeException("not support opera.");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (isQueue) // 如果是以MQ为引擎，则不需要进行异常处理，直接上MQ控制台查看异常
            return result;

        if (!result) {
            record.setField("__table", tableCode);
            record.setField("__opera", opera);
            record.setField("__error", error + 1);
            if (error < 5) {
                queue.repush(session, record);
                log.warn("sync {}.{} fail, times {}, record {}", tableCode, opera, error, record);
            } else {
                processer.abortRecord(record, SyncOpera.values()[opera]);
            }
        }
        return result;
    }

    public ISyncServer getQueue() {
        return queue;
    }

}
