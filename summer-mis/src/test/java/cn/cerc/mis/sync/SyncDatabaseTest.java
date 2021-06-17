package cn.cerc.mis.sync;

import cn.cerc.core.Record;
import cn.cerc.mis.custom.SessionDefault;

public class SyncDatabaseTest {

    public static void main(String[] args) {
        Record record = new Record();
        record.setField("code", "a01");
        SyncDatabase db = new SyncDatabase(new SyncQueueTest());
        db.push(new SessionDefault(), "part", record, SyncOpera.Update);
    }

}
