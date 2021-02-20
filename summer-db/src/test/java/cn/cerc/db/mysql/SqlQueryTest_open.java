package cn.cerc.db.mysql;

import cn.cerc.db.core.StubHandleText;
import org.junit.Before;
import org.junit.Test;

public class SqlQueryTest_open {
    private SqlQuery ds;
    private StubHandleText handl;

    @Before
    public void setUp() {
        handl = new StubHandleText();
        ds = new SqlQuery(handl);
        ds.getSqlText().setMaximum(1);
        ds.add("select CorpNo_,CWCode_,PartCode_ from TranB1B where CorpNo_='%s'", "911001");
    }

    @Test(expected = RuntimeException.class)
    public void test_locked() {
        ds.getFieldDefs().add("CorpNo_");
        ds.getFieldDefs().add("CWCode_");
        // 仅定义了2个字段即锁定
        ds.getFieldDefs().setLocked(true);
        ds.open();
    }
}
