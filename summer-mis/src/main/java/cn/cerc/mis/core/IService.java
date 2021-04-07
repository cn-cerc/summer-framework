package cn.cerc.mis.core;

import cn.cerc.core.DataSet;

public interface IService extends IDataService {

    IStatus execute(DataSet dataIn, DataSet dataOut) throws ServiceException;

}
