package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;

public interface IDataService extends IHandle {

    IHandle getHandle();

    // 数据库连接
    void setHandle(IHandle handle);

}
