package cn.cerc.mis.custom;

import org.springframework.stereotype.Component;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.client.IServiceProxy;
import cn.cerc.mis.core.IServiceProxyFactory;
import cn.cerc.mis.core.LocalService;

@Component
public class ServiceProxyFactoryDefault implements IServiceProxyFactory {

    private ISession session;

    @Override
    public IServiceProxy get(IHandle handle, String corpNo) {
        
        return new LocalService(handle);
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

}
