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
    public boolean popRecord(ISession session, Record record) {
        String tableCode = record.getString("__table");
        int opera = record.getInt("__opera");
        int error = record.getInt("__error");
        record.delete("__table");
        record.delete("__opera");
        record.delete("__error");

        IPushProcesser processer = Application.getBean(IPushProcesser.class, "sync_" + tableCode);
        if (processer == null) {
            processer = new PushTableDefault().setTableCode(tableCode);
        }
        processer.setSession(session);

        boolean result;
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

        if (!result) {
            record.setField("__table", tableCode);
            record.setField("__opera", opera);
            record.setField("__error", error + 1);
            if (error < 5) {
                queue.repush(session, record);
                log.warn("sync {}.{} fail, times {}", tableCode, opera, error);
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
