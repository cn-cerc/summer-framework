package cn.cerc.mis.queue;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.HandleDefault;
import cn.cerc.mvc.SummerMVC;

public class AsyncServiceTest {

    @Test
    @Ignore
    public void test_send_get() {
        Application.init(SummerMVC.ID);
        ISession session = Application.createSession();
        AsyncService app = new AsyncService(new HandleDefault(session));
        app.setService("TAppCreditLine.calCusCreditLimit");
        // app.setTimer(TDateTime.now().getTime());
        app.getDataIn().getHead().setField("UserCode_", session.getUserCode());
        app.setSubject("回算信用额度");
        assertTrue("发送消息失败", app.exec());
    }
}
