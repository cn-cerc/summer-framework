package cn.cerc.mis.client;

import cn.cerc.core.DataSet;

public interface IServiceProxy {
    public static final String _message_ = "_message_";

    // 返回服务代码
    String getService();

    // 设置服务代码
    Object setService(String service);

    // 传入数据
    DataSet getDataIn();

    // 返回数据
    DataSet getDataOut();

    // 提示讯息
    String getMessage();

    // 执行
    boolean exec(Object... args);
//
//    // 服务缓存
//    default String getExportKey() {
//        return null;
//    }
//
//    // select
//    default boolean get(Object... args) {
//        Record headIn = this.getDataIn().getHead();
//        headIn.setField("_method_", "get");
//        return exec(args);
//    }
//
//    // append
//    default boolean post(Object... args) {
//        Record headIn = this.getDataIn().getHead();
//        headIn.setField("_method_", "post");
//        return exec(args);
//    }
//
//    // update
//    default boolean put(Object... args) {
//        Record headIn = this.getDataIn().getHead();
//        headIn.setField("_method_", "put");
//        return exec(args);
//    }
//
//    // delete
//    default boolean delete(Object... args) {
//        Record headIn = this.getDataIn().getHead();
//        headIn.setField("_method_", "delete");
//        return exec(args);
//    }

}
