package cn.cerc.mis.core;

import cn.cerc.mis.rds.StubHandle;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LocalServiceTest {
    private StubHandle handle;

    @Before
    public void setUp() {
        handle = new StubHandle();
    }

    @Test
    @Ignore
    public void test() {
        LocalService app = new LocalService(handle);
        app.setService("SvrUserLogin.check");
        System.out.println(app.exec());
        System.out.println(app.getMessage());
    }

}
