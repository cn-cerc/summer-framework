package cn.cerc.vine.core;

import cn.cerc.db.core.IHandle;
import cn.cerc.mis.client.RemoteService;

public class PartnerService extends RemoteService {

    private String corpNo;

    public PartnerService() {
        super();
    }

    public PartnerService(IHandle handle) {
        super(handle);
    }

    public PartnerService(IHandle handle, String service) {
        super(handle);
        this.setService(service);
    }

    public String getCorpNo() {
        return corpNo;
    }

    public void setCorpNo(String corpNo) {
        this.corpNo = corpNo;
    }

}
