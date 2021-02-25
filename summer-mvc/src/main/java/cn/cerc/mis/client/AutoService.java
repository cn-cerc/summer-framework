package cn.cerc.mis.client;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.BookHandle;
import cn.cerc.mis.core.IService;
import cn.cerc.mis.core.IStatus;
import cn.cerc.mis.core.ServiceException;

public class AutoService {
    private DataSet dataOut = new DataSet();
    private String message;
    private IHandle handle;
    private ServiceRecord service;

    public AutoService(IHandle handle, String corpNo, String userCode, String service) {
        this.handle = handle;
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
            throw new RuntimeException("not specified service");
        }

        // handle.init(service.getCorpNo(), service.getUserCode(), "127.0.0.1");

        BookHandle handle = new BookHandle(this.handle, service.getCorpNo());
        handle.setUserCode(service.getUserCode());

        // 根据xml进行反射初始化服务信息
        IService bean = Application.getService(handle, service.getService());
        if (bean == null) {
            throw new RuntimeException(String.format("could not create service：%s" , service.getService()));
        }

        IStatus status = bean.execute(service.getDataIn(), dataOut);

        boolean result = status.getResult();
        this.setMessage(status.getMessage());
        return result;
    }

    public ServiceRecord getService() {
        return this.service;
    }

    public class ServiceRecord implements AutoCloseable {
        private String service;
        private String corpNo;
        private String userCode;
        private String error_email;
        private String error_subject;
        private DataSet dataIn;

        public ServiceRecord() {
            super();
            this.dataIn = new DataSet();
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public DataSet getDataIn() {
            return this.dataIn;
        }

        @Override
        public void close() {
            this.dataIn.close();
        }

        public String getError_email() {
            return error_email;
        }

        public void setError_email(String error_email) {
            this.error_email = error_email;
        }

        public String getError_subject() {
            return error_subject;
        }

        public void setError_subject(String error_subject) {
            this.error_subject = error_subject;
        }

        public String getCorpNo() {
            return corpNo;
        }

        public void setCorpNo(String corpNo) {
            this.corpNo = corpNo;
        }
    }

}
