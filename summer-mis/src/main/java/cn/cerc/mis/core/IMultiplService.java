package cn.cerc.mis.core;

import cn.cerc.core.DataSet;

public interface IMultiplService extends IService {

    DataSet getDataIn();

    DataSet getDataOut();

    void setDataIn(DataSet dataIn);

    void setDataOut(DataSet dataOut);

    String getFuncCode();
    
    void setFuncCode(String funcCode);
}
