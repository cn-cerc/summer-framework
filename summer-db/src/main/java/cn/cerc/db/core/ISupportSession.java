package cn.cerc.db.core;

import cn.cerc.core.ISession;

public interface ISupportSession {
    
    void setSession(ISession session);

    ISession getSession();
}
