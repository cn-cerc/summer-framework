package cn.cerc.mis.core;

import cn.cerc.core.DataSet;
import cn.cerc.db.core.Handle;
import cn.cerc.db.core.IHandle;

public abstract class CustomLocalProxy extends Handle {
    private String service;
    private String message;

    public CustomLocalProxy(IHandle handle) {
        super(handle);
    }

    protected boolean executeService(Object bean, DataSet dataIn, DataSet dataOut) {
        IStatus status;
        IService ss = (IService) bean;
        try {
            status = ss.execute(dataIn, dataOut);
        } catch (ServiceException e) {
            status = new ServiceStatus(false, e.getMessage());
        }
        this.setMessage(status.getMessage());
        return status.getResult();
    }

    protected Object getServiceObject() {
        if (getSession() == null) {
            this.setMessage("session is null.");
            return null;
        }
        if (getService() == null) {
            this.setMessage("service is null.");
            return null;
        }

        try {
            return Application.getService(this, getService());
        } catch (ClassNotFoundException e) {
            this.setMessage(e.getMessage());
            return null;
        }
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

}
