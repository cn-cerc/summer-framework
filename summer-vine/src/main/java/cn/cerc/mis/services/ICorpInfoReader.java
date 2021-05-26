package cn.cerc.mis.services;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;

public interface ICorpInfoReader {

    /**
     * 取得指定的帐套讯息
     */
    Record getCorpInfo(ISession session, String corpNo);

}
