package cn.cerc.mis.core;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.client.RemoteService;

public class CenterService extends RemoteService {

    public CenterService() {
        super();
    }

    public CenterService(IHandle handle) {
        super(handle);
    }

    public CenterService(IHandle handle, String service) {
        super(handle);
        this.setService(service);
    }

}
