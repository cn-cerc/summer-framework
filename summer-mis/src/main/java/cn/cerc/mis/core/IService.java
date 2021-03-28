package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SupportHandle;

public interface IService extends IDataService, SupportHandle {
    IStatus execute(DataSet dataIn, DataSet dataOut) throws ServiceException;

    default boolean checkSecurity(IHandle handle) {
        ISession sess = handle.getSession();
        return sess != null && sess.logon();
    }

    // 主要适用于Delphi Client调用
    default String getJSON(DataSet dataOut) {
        return String.format("[%s]", dataOut.getJSON());
    }

    @Deprecated
    @Override
    default void init(IHandle handle) {
        setHandle(handle);
    }

}
