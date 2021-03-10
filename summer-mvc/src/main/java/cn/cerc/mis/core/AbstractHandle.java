package cn.cerc.mis.core;

import cn.cerc.core.ClassResource;
import cn.cerc.core.IHandle;
import cn.cerc.db.mysql.MysqlConnection;

public class AbstractHandle implements IHandle {

    private static final ClassResource res = new ClassResource(AbstractHandle.class, "summer-mvc");

    protected IHandle handle;

    public MysqlConnection getConnection() {
        return (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
    }

    @Override
    public String getCorpNo() {
        return handle.getCorpNo();
    }

    @Override
    public String getUserCode() {
        return handle.getUserCode();
    }

    @Override
    public Object getProperty(String key) {
        return handle.getProperty(key);
    }

    public IHandle getHandle() {
        return handle;
    }

    public void setHandle(IHandle handle) {
        this.handle = handle;
    }

    @Override
    public String getUserName() {
        return handle.getUserName();
    }

    @Override
    public void setProperty(String key, Object value) {
        throw new RuntimeException(res.getString(1, "调用了未被实现的接口"));
    }

    @Override
    public boolean init(String bookNo, String userCode, String password, String clientCode) {
        throw new RuntimeException(res.getString(1, "调用了未被实现的接口"));
    }

    @Override
    public boolean init(String token) {
        throw new RuntimeException(res.getString(1, "调用了未被实现的接口"));
    }

    @Override
    public boolean logon() {
        return false;
    }

    @Override
    public void close() {
    }

}
