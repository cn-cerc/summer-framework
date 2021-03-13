package cn.cerc.mis.core;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;

public class HandleDefault implements IHandle{

    private ISession session;
    
    public HandleDefault(ISession session) {
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

}
