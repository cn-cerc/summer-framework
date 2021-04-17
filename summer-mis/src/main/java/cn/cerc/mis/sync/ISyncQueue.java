package cn.cerc.mis.sync;

import cn.cerc.core.Record;

public interface ISyncQueue {

    void push(Record record);

    Record pop();

    void repush(Record record);
    
}
