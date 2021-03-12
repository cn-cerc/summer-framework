package cn.cerc.db.core;

import cn.cerc.core.ISession;

public class CustomBean implements IHandle {

    private ISession session;
    protected IHandle handle;

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    public void setHandle(IHandle handle) {
        this.handle = handle;
        if (handle != null) {
            this.setSession(handle.getSession());
        }
    }

    public IHandle getHandle() {
        return this.handle;
    }
}
