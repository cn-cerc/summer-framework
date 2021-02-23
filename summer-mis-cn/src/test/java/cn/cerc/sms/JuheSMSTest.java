package cn.cerc.sms;

import org.junit.Before;
import org.junit.Test;

public class JuheSMSTest {

    private JuheSMS juhe;

    @Before
    public void setUp() {
        juhe = new JuheSMS("f9b2a1908a2b61f3cf1aed374c780754");
    }

    @Test
    public void testSend() {
        juhe.send("15915438774", "87616", "#code#=431515");
    }

}
