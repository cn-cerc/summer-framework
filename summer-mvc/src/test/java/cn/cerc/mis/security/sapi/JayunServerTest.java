package cn.cerc.mis.security.sapi;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.mis.security.sapi.JayunServer;

public class JayunServerTest {
    private JayunServer api = new JayunServer(null);

    @Test
    @Ignore
    public void testGetIP() {
        api.getIP();
        assertEquals(api.getMessage(), "127.0.0.1");
    }

}
