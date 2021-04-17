package cn.cerc.mis.sync;

import cn.cerc.core.Record;

public class SyncQueueTest implements ISyncQueue {

    @Override
    public void push(Record record) {
        System.out.println("push:" + record);
    }

    @Override
    public Record pop() {
        System.err.println("not yet pop");
        return null;
    }

    @Override
    public void repush(Record record) {
        System.out.println("repush:" + record);
    }

}
