package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SupportHandle;

public interface IDataService extends IHandle, SupportHandle {

    IHandle getHandle();

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

    // 主要适用于Delphi Client调用
    default boolean checkSecurity(IHandle handle) {
        ISession sess = handle.getSession();
        return sess != null && sess.logon();
    }

    // 主要适用于Delphi Client调用
    default String getJSON(DataSet dataOut) {
        return String.format("[%s]", dataOut.getJSON());
    }

}
