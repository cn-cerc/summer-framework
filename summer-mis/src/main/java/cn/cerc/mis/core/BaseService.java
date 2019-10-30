package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.core.SupportHandle;

public class BaseService implements SupportHandle {
    protected IHandle handle;

    @Override
    public void init(IHandle handle) {
        this.handle = handle;
    }
}
