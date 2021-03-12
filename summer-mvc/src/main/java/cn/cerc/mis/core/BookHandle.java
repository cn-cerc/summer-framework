package cn.cerc.mis.core;

import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.MysqlConnection;
import cn.cerc.mvc.SummerMVC;

public class BookHandle extends IHandle implements ISession {
    private static final ClassResource res = new ClassResource(BookHandle.class, SummerMVC.ID);

    private String corpNo;
    private String userCode;
    private String userName;

    public BookHandle(IHandle handle, String corpNo) {
        this.setHandle(handle);
        this.corpNo = corpNo;
    }

    @Override
    public String getCorpNo() {
        return this.corpNo;
    }

    @Override
    public String getUserCode() {
        return userCode != null ? userCode : getSession().getUserCode();
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public String getUserName() {
        return userName != null ? userName : getSession().getUserName();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Object getProperty(String key) {
        return getSession().getProperty(key);
    }

    @Override
    public void setProperty(String key, Object value) {
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
