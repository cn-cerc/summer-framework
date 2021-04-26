package cn.cerc.mis.core;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;

public class BookHandleTest {

    @Test
    @Ignore
    public void test() {
        Application.initOnlyFramework();
        ISession session = Application.createSession();
        IHandle app = new BookHandle(new Handle(session), "144001");
        assertEquals(app.getCorpNo(), "144001");
    }

}
