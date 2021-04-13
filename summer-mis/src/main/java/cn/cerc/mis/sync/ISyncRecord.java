package cn.cerc.mis.sync;

import cn.cerc.core.Record;
import cn.cerc.db.core.ISessionOwner;

public interface ISyncRecord extends ISessionOwner {

    boolean appendRecord(Record record);

    boolean deleteRecord(Record record);

    boolean updateRecord(Record record);

    boolean resetRecord(Record record);

    void abortRecord(Record record, SyncOpera opera);

}
