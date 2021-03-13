package cn.cerc.db.core;

import cn.cerc.core.ISession;

public interface ISessionOwner {

    ISession getSession();
    
    void setSession(ISession session);

}
