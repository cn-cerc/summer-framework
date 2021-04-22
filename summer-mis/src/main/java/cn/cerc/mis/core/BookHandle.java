package cn.cerc.mis.core;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.IHandleOwner;

public class BookHandle implements IHandle, IHandleOwner {
    private ISession owner;
    private ISession session;
    private Map<String, Object> params = new HashMap<>();

    public BookHandle(IHandle handle, String corpNo) {
        this.owner = handle.getSession();

        this.session = new ISession() {
            @Override
            public Object getProperty(String key) {
                if (params.containsKey(key))
                    return params.get(key);
                else
                    return owner.getProperty(key);
            }

            @Override
            public void setProperty(String key, Object value) {
                params.put(key, value);
            }

            @Override
            public boolean logon() {
                return owner.logon();
            }

            @Override
            public void close() {
                owner.close();
            }

        };

        session.setProperty(ISession.CORP_NO, corpNo);
    }

    public void setUserCode(String userCode) {
        session.setProperty(ISession.USER_CODE, userCode);
    }

    public void setUserName(String userName) {
        session.setProperty(ISession.USER_NAME, userName);
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        throw new RuntimeException("BookHandle not support setSession.");
    }

    @Override
    public IHandle getHandle() {
        return this;
    }

    @Override
    public void setHandle(IHandle handle) {
        throw new RuntimeException("BookHandle not support setHandle.");
    }

}
