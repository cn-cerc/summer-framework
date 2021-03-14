package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.ISessionOwner;
import cn.cerc.mis.client.IServiceProxy;

public interface IServiceProxyFactory extends ISessionOwner{
    
    /**
     * 创建对象，返回 LocalService 或 RemoteService
     * @param handle
     * @param corpNo
     * @return
     */
    IServiceProxy get(IHandle handle, String corpNo);
}
