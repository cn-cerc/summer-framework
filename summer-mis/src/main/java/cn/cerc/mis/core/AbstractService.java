package cn.cerc.mis.core;

import org.springframework.beans.factory.annotation.Autowired;

//@Component
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractService extends Handle implements IService, IRestful {
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

}
