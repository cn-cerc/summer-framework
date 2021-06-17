package cn.cerc.db.core;

import com.google.gson.annotations.Expose;

import cn.cerc.core.ISession;

public class Handle implements IHandle {

    @Expose(serialize = false, deserialize = false)
    private ISession session;

    public Handle() {

    }

    public Handle(ISession session) {
        this.session = session;
    }

    public Handle(IHandle handle) {
        this.session = handle.getSession();
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    @Override
    public ISession getSession() {
        return session;
    }

}
