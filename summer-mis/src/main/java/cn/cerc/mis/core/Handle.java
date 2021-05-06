package cn.cerc.mis.core;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;

public class Handle implements IHandle {

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
