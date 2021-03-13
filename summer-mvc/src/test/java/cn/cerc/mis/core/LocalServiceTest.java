package cn.cerc.mis.core;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.mvc.SummerMVC;

public class LocalServiceTest {
    private ISession session;

    @Before
    public void setUp() {
        Application.init(SummerMVC.ID);
        session = Application.createSession();
    }

    @Test
    @Ignore
    public void test() {
        LocalService app = new LocalService(new Handle(session));
        app.setService("SvrUserLogin.check");
        System.out.println(app.exec());
        System.out.println(app.getMessage());
    }

}
