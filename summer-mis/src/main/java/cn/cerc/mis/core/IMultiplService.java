package cn.cerc.mis.core;

import cn.cerc.core.DataSet;

public interface IMultiplService extends IDataService {

    DataSet getDataIn();

    DataSet getDataOut();

    void setDataIn(DataSet dataIn);

    void setDataOut(DataSet dataOut);

    String getFuncCode();
    
    void setFuncCode(String string);

    public IStatus executeService();
}
