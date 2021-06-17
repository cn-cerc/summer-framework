package cn.cerc.mis.sync;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;

public interface ISyncServer {

    void push(ISession session, Record record);

    void repush(ISession session, Record record);

    int pop(ISession session, IPopProcesser popProcesser, int maxRecords);

}
