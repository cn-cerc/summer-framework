package cn.cerc.mis.core;

import cn.cerc.core.ClassConfig;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.client.RemoteService;

/**
 * 调用中心数据库权限等服务
 * 
 * @author ZhangGong
 *
 */
public class CenterService extends RemoteService {
    private static final ClassConfig config = new ClassConfig(CenterService.class, SummerMIS.ID);

    @Deprecated
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

    @Override
    public boolean exec(Object... args) {
        String site = config.getClassProperty("site", null);
        if (site != null) {
            String url = String.format("%s?service=%s", site, this.getService());
            return this.executeService(url);
        } else {
            this.initDataIn(args);
            LocalService svr = new LocalService(this.getHandle());
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

}
