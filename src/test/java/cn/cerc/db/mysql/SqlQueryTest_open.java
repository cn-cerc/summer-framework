package cn.cerc.db.mysql;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class SqlQueryTest_open implements IHandle {
    private SqlQuery ds;
    private ISession session;

    @Before
    public void setUp() {
        session = new StubSession();
        ds = new SqlQuery(this);
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

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }
}
