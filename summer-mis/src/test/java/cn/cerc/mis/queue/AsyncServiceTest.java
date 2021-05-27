package cn.cerc.mis.queue;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.Handle;
import cn.cerc.mis.core.Application;

public class AsyncServiceTest {

    @Test
    @Ignore
    public void test_send_get() {
        Application.initOnlyFramework();
        ISession session = Application.getSession();
        AsyncService app = new AsyncService(new Handle(session));
        app.setService("TAppCreditLine.calCusCreditLimit");
        // app.setTimer(TDateTime.now().getTime());
        app.getDataIn().getHead().setField("UserCode_", session.getUserCode());
        app.setSubject("回算信用额度");
        assertTrue("发送消息失败", app.exec());
    }
}
