package cn.cerc.db.mysql;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.core.PostFieldException;
import cn.cerc.core.TDateTime;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class SqlQueryTest_post1 implements IHandle {
    private MysqlQuery ds;
    private ISession session;

    @Before
    public void setUp() {
        session = new StubSession();
        ds = new MysqlQuery(this);
    }

    @Test(expected = PostFieldException.class)
    @Ignore(value = "仅允许在测试数据库运行")
    public void post_error() {
        ds.getFieldDefs().add("Test");
        ds.add("select * from Dept where CorpNo_='%s'", "144001");
        ds.open();
        ds.edit();
        ds.setField("updateDate_", TDateTime.now().incDay(-1));
        ds.post();
    }

    @Test()
    @Ignore(value = "仅允许在测试数据库运行")
    public void post() {
        ds.add("select * from Dept where CorpNo_='%s'", "144001");
        ds.open();
        ds.onBeforePost(ds -> {
            System.out.println("before post");
        });
        ds.edit();
        ds.setField("Test", "aOK");
        ds.setField("UpdateDate_", TDateTime.now().incDay(-1));
        ds.post();
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
