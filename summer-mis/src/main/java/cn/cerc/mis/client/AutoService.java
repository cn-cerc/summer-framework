package cn.cerc.mis.client;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IService;
import cn.cerc.mis.core.IStatus;
import cn.cerc.mis.core.ServiceException;

public class AutoService {
    private DataSet dataOut = new DataSet();
    private String message;
    private IHandle handle;
    private ServiceRecord service;

    public AutoService() {
        handle = Application.getHandle();
    }

    public AutoService(ServiceRecord service) {
        this.service = service;
        handle = Application.getHandle();
    }

    public AutoService(String corpNo, String userCode, String service) {
        handle = Application.getHandle();
        this.service = new ServiceRecord();
        this.service.setCorpNo(corpNo);
        this.service.setUserCode(userCode);
        this.service.setService(service);
    }

    public DataSet getDataIn() {
        return service.getDataIn();
    }

    public DataSet getDataOut() {
        return dataOut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean exec() throws ServiceException {
        if (service.getService() == null) {
            throw new RuntimeException("没有指定 service");
        }

        // handle.init(service.getCorpNo(), service.getUserCode(), "127.0.0.1");

        // 直接设置用户信息到 handle 中
        handle.setProperty(Application.bookNo, service.getCorpNo());
        handle.setProperty(Application.userCode, service.getUserCode());
        handle.setProperty(Application.clientIP, "127.0.0.1");
        IService bean = Application.getService(handle, service.getService());
        if (bean == null) {
            throw new RuntimeException("无法创建服务：" + service.getService());
        }

        IStatus status = bean.execute(service.getDataIn(), dataOut);

        boolean result = status.getResult();
        this.setMessage(status.getMessage());
        return result;
    }

    public ServiceRecord getService() {
        return this.service;
    }

}
