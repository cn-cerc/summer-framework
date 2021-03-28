package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.db.core.IHandle;

public abstract class CustomLocalProxy {
    private IHandle handle;
    private String service;
    private String message;

    public CustomLocalProxy(IHandle handle) {
        this.handle = handle;
        if (handle == null) {
            throw new RuntimeException("handle is null.");
        }
    }

    protected boolean executeService(Object bean, DataSet dataIn, DataSet dataOut) {
        IStatus status;
        if (bean instanceof IService) {
            IService ss = (IService) bean;
            try {
                status = ss.execute(dataIn, dataOut);
            } catch (ServiceException e) {
                status = new ServiceStatus(false, e.getMessage());
            }
        } else if (bean instanceof IMultiplService) {
            IMultiplService ms = (IMultiplService) bean;
            ms.setDataIn(dataIn);
            ms.setDataOut(dataOut);
            status = ms.execute();
        } else {
            status = new ServiceStatus(false, String.format("not support: %s ！", bean.getClass().getName()));
        }
        this.setMessage(status.getMessage());
        return status.getResult();
    }

    protected Object getServiceObject() {
        if (getHandle() == null) {
            this.setMessage("handle is null.");
            return null;
        }
        if (getService() == null) {
            this.setMessage("service is null.");
            return null;
        }

        // 读取xml中的配置
        Object bean = Application.getContext().getBean(this.getService());
        if (bean == null) {
            // 读取注解的配置
            String beanId = getService().split("\\.")[0];
            beanId = beanId.substring(0, 1).toLowerCase() + beanId.substring(1);
            // 支持指定函数
            bean = Application.getBean(IService.class, beanId);
            if (bean instanceof CustomService) {
                CustomService cs = ((CustomService) bean);
                cs.setFuncCode(getService().split("\\.")[1]);
            }
        }
        if (bean == null) {
            this.setMessage(String.format("bean %s not find", getService()));
            return null;
        }

        if (bean instanceof IDataService) {
            ((IDataService) bean).setHandle(handle);
        }
        return bean;
    }

    public String getService() {
        return service;
    }

    public CustomLocalProxy setService(String service) {
        this.service = service;
        return this;
    }

    public String getMessage() {
        if (message != null) {
            return message.replaceAll("'", "\"");
        } else {
            return null;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public IHandle getHandle() {
        return handle;
    }

    public void setHandle(IHandle handle) {
        this.handle = handle;
    }

}
