package cn.cerc.db.core;

public interface IHandleOwner {

    void setHandle(IHandle handle);
    
    IHandle getHandle();

    @Deprecated
    default void init(IHandle handle) {
        setHandle(handle);
    }
}
