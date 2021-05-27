package cn.cerc.mis.rds;

import cn.cerc.core.ISession;
import cn.cerc.db.core.Handle;
import cn.cerc.mis.core.Application;

public class StubHandle extends Handle {
    
    // FIXME 此处应该使用ClassConfig
    public static final String DefaultBook = "999001";
    public static final String DefaultUser = DefaultBook + "01";
    public static final String DefaultProduct = "999001000001";
    public static final String password = "123456";
    public static final String machineCode = "T800";

    // 生产部
    public static final String DefaultDept = "10050001";

    public StubHandle() {
        Application.initOnlyFramework();
        this.setSession(Application.getSession());
    }
    
    public StubHandle(String corpNo, String userCode) {
        Application.initOnlyFramework();
        this.setSession(Application.getSession());
        getSession().setProperty(ISession.CORP_NO, corpNo);
        getSession().setProperty(ISession.USER_CODE, userCode);
    }

}
