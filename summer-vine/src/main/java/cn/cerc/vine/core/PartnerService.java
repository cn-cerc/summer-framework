package cn.cerc.vine.core;

import cn.cerc.core.ClassConfig;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.client.RemoteService;
import cn.cerc.mis.core.BookHandle;
import cn.cerc.mis.core.CenterService;
import cn.cerc.mis.core.LocalService;
import cn.cerc.vine.SummerVine;

/**
 * 调用地藤上下游服务
 * 
 * @author ZhangGong
 *
 */
public class PartnerService extends RemoteService {
    private static final ClassConfig config = new ClassConfig(CenterService.class, SummerVine.ID);

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

    @Override
    public boolean exec(Object... args) {
        String site = config.getClassProperty("site", null);
        if (site != null) {
            String url = String.format("%s?corpNo=%s&service=%s", site, this.corpNo, this.getService());
            return this.executeService(url);
        } else {
            this.initDataIn(args);
            LocalService svr = new LocalService(new BookHandle(this.getHandle(), this.corpNo));
            svr.setService(this.getService());
            svr.setDataIn(getDataIn());
            boolean result = svr.exec();
            this.setDataOut(svr.getDataOut());
            if (!result) {
                this.setMessage(svr.getMessage());
            }
            return result;
        }
    }

    public String getCorpNo() {
        return corpNo;
    }

    public void setCorpNo(String corpNo) {
        this.corpNo = corpNo;
    }

}
