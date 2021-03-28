package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;

public interface IDataService extends IHandle {

    IHandle getHandle();

    // 数据库连接
    void setHandle(IHandle handle);

    default ServiceStatus fail(String format, Object... args) {
        ServiceStatus status = new ServiceStatus(false);
        if (args.length > 0) {
            status.setMessage(String.format(format, args));
        } else {
            status.setMessage(format);
        }
        return status;
    }

    default ServiceStatus success() {
        return new ServiceStatus(true);
    }

    default ServiceStatus success(String format, Object... args) {
        ServiceStatus status = new ServiceStatus(true);
        if (args.length > 0) {
            status.setMessage(String.format(format, args));
        } else {
            status.setMessage(format);
        }
        return status;
    }

}
