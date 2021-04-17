package cn.cerc.mis.sync;

import cn.cerc.core.Record;

public class SyncDatabaseTest {

    public static void main(String[] args) {
        Record record = new Record();
        record.setField("code", "a01");
        SyncDatabase db = new SyncDatabase(new SyncQueueTest());
        db.push("part", record, SyncOpera.Update);
    }

}
