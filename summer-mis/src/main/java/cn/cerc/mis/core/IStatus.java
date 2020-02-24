package cn.cerc.mis.core;

public interface IStatus {
    // 服务执行结果状态,默认：2xx=ok, 1xx=fail
    int getStatus();

    boolean getResult();

    String getMessage();
}
