package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;

public class LocalProxy extends CustomLocalProxy {

    public LocalProxy(IHandle handle) {
        super(handle);
    }

    public DataSet exec(Record headIn) {
        Object bean = this.getServiceObject();
        if (bean == null)
            return null;

        DataSet dataIn = new DataSet();
        dataIn.getHead().copyValues(headIn);
        DataSet dataOut = new DataSet();
        if (executeService(bean, dataIn, dataOut))
            return dataOut;
        else
            return null;
    }

    public DataSet exec(DataSet dataIn) {
        Object bean = this.getServiceObject();
        if (bean == null)
            return null;

        DataSet dataOut = new DataSet();
        if (executeService(bean, dataIn, dataOut))
            return dataOut;
        else
            return null;
    }

}
