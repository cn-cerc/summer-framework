package cn.cerc.db.core;

@Deprecated
public class IHandle extends CustomHandle {

    protected IHandle handle;

    public boolean logon() {
        return getSession().logon();
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
