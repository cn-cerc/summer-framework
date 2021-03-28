package cn.cerc.mis.core;

import org.springframework.beans.factory.annotation.Autowired;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;

public abstract class AbstractService implements IService, IRestful {
    @Autowired
    public ISystemTable systemTable;
    private String restPath;
    private ISession session;
    protected IHandle handle;

    @Override
    public String getRestPath() {
        return restPath;
    }

    @Override
    public void setRestPath(String restPath) {
        this.restPath = restPath;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
        if (handle == null)
            handle = new Handle(session);
    }

    @Override
    public void setHandle(IHandle handle) {
        this.handle = handle;
        if (handle != null) {
            this.setSession(handle.getSession());
        }
    }

    @Override
    public IHandle getHandle() {
        return this.handle;
    }

}
