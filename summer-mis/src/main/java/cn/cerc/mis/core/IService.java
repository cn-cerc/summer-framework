package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SupportHandle;

public interface IService extends IDataService, SupportHandle {
    IStatus execute(DataSet dataIn, DataSet dataOut) throws ServiceException;

    @Deprecated
    @Override
    default void init(IHandle handle) {
        setHandle(handle);
    }

}
