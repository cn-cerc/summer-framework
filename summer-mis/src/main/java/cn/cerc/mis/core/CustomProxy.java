package cn.cerc.mis.core;

import cn.cerc.core.DataSet;

public abstract class CustomProxy {
    
    public abstract String getService();
    
    public abstract DataSet getDataIn();
    
    public abstract DataSet getDataOut();
    
    public abstract String getMessage();
}
