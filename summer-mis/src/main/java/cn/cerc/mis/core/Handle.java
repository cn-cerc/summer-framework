package cn.cerc.mis.core;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.IHandleOwner;

public class Handle implements IHandle, IHandleOwner {

    protected IHandle handle;
    private ISession session;

    public Handle() {

    }

    public Handle(ISession session) {
        this.session = session;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
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
