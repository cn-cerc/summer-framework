package cn.cerc.mis.client;

import cn.cerc.core.DataSet;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.BookHandle;
import cn.cerc.mis.core.CustomLocalProxy;
import cn.cerc.mis.core.CustomService;
import cn.cerc.mis.core.ServiceException;

public class AutoService extends CustomLocalProxy {
    private DataSet dataOut = new DataSet();
    private String message;
    private IHandle handle;
    private ServiceRecord service;

    public AutoService(IHandle handle, String corpNo, String userCode, String service) {
        super(handle);
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

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    public boolean exec() throws ServiceException {
        if (service.getService() == null) {
            throw new RuntimeException("not specified service");
        }

        Object object = getServiceObject();
        if (object == null) {
            return false;
        }

        BookHandle handle = new BookHandle(this.handle, service.getCorpNo());
        handle.setUserCode(service.getUserCode());
        if (object instanceof CustomService) {
            ((CustomService) object).setHandle(handle);
        }
        boolean result = executeService(object, service.getDataIn(), dataOut);

        this.setMessage(getMessage());
        return result;
    }

    @Override
    public String getService() {
        return this.service.getService();
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
