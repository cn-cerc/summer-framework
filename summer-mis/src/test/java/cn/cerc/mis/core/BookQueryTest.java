package cn.cerc.mis.core;

import cn.cerc.core.ISession;
import cn.cerc.core.TDateTime;

import org.junit.Ignore;
import org.junit.Test;

public class BookQueryTest {

    @Test(expected = RuntimeException.class)
    @Ignore
    public void test() {
        ISystemTable systemTable = Application.getSystemTable();
        Application.initOnlyFramework();
        ISession session = Application.getSession();
        BookQuery ds = new BookQuery(new Handle(session));
        ds.add("select * from %s where CorpNo_='144001'", systemTable.getBookInfo());
        ds.open();
        ds.edit();
        ds.setField("UpdateKey_", TDateTime.now());
        ds.post();
    }

}
