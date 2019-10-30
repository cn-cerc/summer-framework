package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.db.mysql.MysqlConnection;

public class BookHandle implements IHandle {
    private IHandle handle;
    private String corpNo;
    private String userCode;
    private String userName;

    public BookHandle(IHandle handle, String corpNo) {
        this.handle = handle;
        this.corpNo = corpNo;
    }

    public MysqlConnection getConnection() {
        return (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
    }

    @Override
    public String getCorpNo() {
        return this.corpNo;
    }

    @Override
    public String getUserCode() {
        return userCode != null ? userCode : handle.getUserCode();
    }

    @Override
    public String getUserName() {
        return userName != null ? userName : handle.getUserName();
    }

    @Override
    public Object getProperty(String key) {
        return handle.getProperty(key);
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void setProperty(String key, Object value) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    @Override
    public boolean init(String bookNo, String userCode, String clientCode) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    @Override
    public boolean init(String token) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    @Override
    public boolean logon() {
        return false;
    }

    @Override
    public void close() {

    }
}
