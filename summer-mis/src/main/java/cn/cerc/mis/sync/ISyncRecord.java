package cn.cerc.mis.sync;

import cn.cerc.core.Record;

public interface ISyncRecord {

    boolean onAppend(Record newRecord);

    boolean onDelete(Record current);

    boolean onUpdate(Record current, Record newRecord);

}
