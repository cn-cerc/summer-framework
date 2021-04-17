package cn.cerc.mis.sync;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;

public interface IPopProcesser {

    boolean saveRecord(ISession session, Record record);

}
