package cn.cerc.mis.client;

import cn.cerc.core.DataSet;

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
