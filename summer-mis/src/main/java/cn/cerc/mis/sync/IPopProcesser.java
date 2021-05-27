package cn.cerc.mis.sync;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;

public interface IPopProcesser {

    boolean popRecord(ISession session, Record record, boolean isQueue);

}
