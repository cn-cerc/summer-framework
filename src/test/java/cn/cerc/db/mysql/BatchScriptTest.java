package cn.cerc.db.mysql;

import cn.cerc.core.Utils;
import cn.cerc.db.core.StubSession;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BatchScriptTest {
    private StubSession handle;
    private BatchScript bs;

    @Before
    public void setUp() {
        handle = new StubSession();
    }

    @Test
    public void test_getItems() {
        bs = new BatchScript(handle);
        bs.add("select * from a");
        bs.addSemicolon();
        bs.add("select * from b");
        String s1 = "select * from a ;" + Utils.vbCrLf + "select * from b ";
        assertEquals(s1, bs.toString());
    }

    @Test
    public void test_exists() {
        bs = new BatchScript(handle);
        bs.add("select * from Account where Code_='%s';", "admin");
        bs.add("select * from Account where Code_='%s';", "99900101");
        bs.exec();
        assertTrue(bs.exists());
    }

    @Test
    public void test_getItem() {
        bs = new BatchScript(handle);
        bs.add("select * from a");
        bs.addSemicolon();
        bs.add("select * from b");
        assertEquals(bs.size(), 2);
        assertEquals(bs.getItem(0), "select * from a");
        assertEquals(bs.getItem(1), "select * from b");
    }

    @Test(expected = RuntimeException.class)
    public void test_getItem_err() {
        bs = new BatchScript(handle);
        bs.add("select * from a");
        bs.addSemicolon();
        bs.add("select * from b");
        assertEquals(bs.size(), 2);
        assertEquals(bs.getItem(2), "select * from a");
    }

    @Test
    public void test_clean() {
        bs = new BatchScript(handle);
        bs.add("select * from a;");
        bs.clean();
        bs.add("select * from b;");
        assertEquals(bs.size(), 1);
    }
}
