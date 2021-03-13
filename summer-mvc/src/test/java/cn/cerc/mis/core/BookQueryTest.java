package cn.cerc.mis.core;

import cn.cerc.core.ISession;
import cn.cerc.core.TDateTime;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.rds.StubHandle;

import org.junit.Ignore;
import org.junit.Test;

public class BookQueryTest {

    @Test(expected = RuntimeException.class)
    @Ignore
    public void test() {
        ISystemTable systemTable = Application.getBeanDefault(ISystemTable.class, null);
        Application.init(SummerMIS.ID);
        ISession session = Application.createSession();
        BookQuery ds = new BookQuery(new Handle(session));
        ds.add("select * from %s where CorpNo_='144001'", systemTable.getBookInfo());
        ds.open();
        ds.edit();
        ds.setField("UpdateKey_", TDateTime.now());
        ds.post();
    }

}
