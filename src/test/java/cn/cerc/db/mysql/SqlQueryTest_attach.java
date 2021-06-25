package cn.cerc.db.mysql;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class SqlQueryTest_attach implements IHandle {
    private MysqlQuery ds;
    private ISession session;

    @Before
    public void setUp() {
        session = new StubSession();
        ds = new MysqlQuery(this);
    }

    @Test
    public void test() {
        String sql = "select * from ourinfo where CorpNo_='%s'";
        ds.attach(String.format(sql, "000000"));
        ds.attach(String.format(sql, "144001"));
        ds.attach(String.format(sql, "911001"));
        for (Record record : ds) {
            System.out.println(record.toString());
        }
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
