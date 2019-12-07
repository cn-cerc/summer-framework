package cn.cerc.mis.client;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;

public interface IServiceProxy {
    public static final String _message_ = "_message_";

    // 设置服务代码
    public IServiceProxy setService(String service);

    // 返回服务代码
    public String getService();

    // 传入数据
    public DataSet getDataIn();

    // 返回数据
    public DataSet getDataOut();

    // 提示讯息
    public String getMessage();

    // 执行
    public boolean exec(Object... args);

    // select
    default public boolean get(Object... args) {
        Record headIn = this.getDataIn().getHead();
        headIn.setField("_method_", "get");
        return exec(args);
    }

    // append
    default public boolean post(Object... args) {
        Record headIn = this.getDataIn().getHead();
        headIn.setField("_method_", "post");
        return exec(args);
    }

    // update
    default public boolean put(Object... args) {
        Record headIn = this.getDataIn().getHead();
        headIn.setField("_method_", "put");
        return exec(args);
    }

    // delete
    default public boolean delete(Object... args) {
        Record headIn = this.getDataIn().getHead();
        headIn.setField("_method_", "delete");
        return exec(args);
    }
}
