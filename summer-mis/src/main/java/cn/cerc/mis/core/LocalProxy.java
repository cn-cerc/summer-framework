package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;

public class LocalProxy extends CustomLocalProxy {

    public LocalProxy(IHandle handle) {
        super(handle);
    }

    public DataSet exec(String... args) {
        Object bean = this.getServiceObject();
        if (bean == null)
            return null;

        DataSet dataIn = new DataSet();
        if (args.length > 0) {
            Record headIn = dataIn.getHead();
            if (args.length % 2 != 0) {
                // TODO 此处应该使用 ClassResource
                throw new RuntimeException("传入的参数数量必须为偶数！");
            }
            for (int i = 0; i < args.length; i = i + 2) {
                headIn.setField(args[i].toString(), args[i + 1]);
            }
        }

        DataSet dataOut = new DataSet();
        if (executeService(bean, dataIn, dataOut))
            return dataOut;
        else
            return null;
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
