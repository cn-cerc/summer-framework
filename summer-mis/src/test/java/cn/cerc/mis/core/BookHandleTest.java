package cn.cerc.mis.core;

import cn.cerc.core.IHandle;
import cn.cerc.mis.rds.StubHandle;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookHandleTest {
    private StubHandle handle = new StubHandle();

    @Test
    @Ignore
    public void test() {
        IHandle app = new BookHandle(handle, "144001");
        assertEquals(app.getCorpNo(), "144001");
    }
}
