package cn.cerc.mis.core;

import cn.cerc.core.TDateTime;
import cn.cerc.mis.rds.StubHandle;
import org.junit.Ignore;
import org.junit.Test;

public class BookQueryTest {

    @Test(expected = RuntimeException.class)
    @Ignore
    public void test() {
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        StubHandle handle = new StubHandle();
        BookQuery ds = new BookQuery(handle);
        ds.add("select * from %s where CorpNo_='144001'", systemTable.getBookInfo());
        ds.open();
        ds.edit();
        ds.setField("UpdateKey_", TDateTime.Now());
        ds.post();
    }

}
