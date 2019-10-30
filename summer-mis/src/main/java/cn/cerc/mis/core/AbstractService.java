package cn.cerc.mis.core;

import org.springframework.beans.factory.annotation.Autowired;

import cn.cerc.core.IHandle;

public abstract class AbstractService extends AbstractHandle implements IService, IRestful {
    private String restPath;
    @Autowired
    public ISystemTable systemTable;

    @Override
    public void setRestPath(String restPath) {
        this.restPath = restPath;
    }

    @Override
    public String getRestPath() {
        return restPath;
    }

    @Override
    public void init(IHandle handle) {
        this.handle = handle;
    }
}
