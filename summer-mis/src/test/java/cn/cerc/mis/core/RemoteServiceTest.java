package cn.cerc.mis.core;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.mis.client.RemoteService;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;

public class RemoteServiceTest {
    // private static final Logger log =
    // Logger.getLogger(RemoteServiceTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @Ignore
    public void test() {
        RemoteService app = new RemoteService();
        // app.setHost("r1.diteng.site");
        app.setService("SvrUserLogin.check");
        DataSet datain = app.getDataIn();
        Record head = datain.getHead();
        head.setField("Account_", "admin");
        head.setField("Password_", "123456");
        head.setField("MachineID_", "webclient");
        boolean result = app.exec();
        assertTrue(app.getMessage(), result);
    }
}
