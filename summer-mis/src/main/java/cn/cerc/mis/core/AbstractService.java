package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService extends AbstractHandle implements IService, IRestful {
    @Autowired
    public ISystemTable systemTable;
    private String restPath;

    @Override
    public String getRestPath() {
        return restPath;
    }

    @Override
    public void setRestPath(String restPath) {
        this.restPath = restPath;
    }

    @Override
    public void init(IHandle handle) {
        this.handle = handle;
    }
}
