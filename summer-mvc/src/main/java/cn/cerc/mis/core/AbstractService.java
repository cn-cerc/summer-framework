package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService extends IHandle implements IService, IRestful {
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

    public void init(IHandle handle) {
        this.setHandle(handle);
    }
}
