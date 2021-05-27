package cn.cerc.mis.services;

import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;

public interface ICorpInfoReader {

    /**
     * 取得指定的帐套讯息
     */
    Record getCorpInfo(IHandle handle, String corpNo);

}
