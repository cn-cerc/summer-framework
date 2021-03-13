package cn.cerc.mis.core;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.mvc.SummerMVC;

public class BookHandleTest {

    @Test
    @Ignore
    public void test() {
        Application.init(SummerMVC.ID);
        ISession session = Application.createSession();
        IHandle app = new BookHandle(new Handle(session), "144001");
        assertEquals(app.getCorpNo(), "144001");
    }

}
