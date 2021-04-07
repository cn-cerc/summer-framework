package cn.cerc.db.core;

public interface SupportHandle {

    void setHandle(IHandle handle);

    default void init(IHandle handle) {
        setHandle(handle);
    }
}
