package cn.cerc.db.mssql;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class MssqlQueryTest implements IHandle {

    private ISession session;

    @Before
    public void setUP() {
        session = new StubSession();
    }

    @Test
    public void test_append() {
        MssqlQuery query = new MssqlQuery(this);
        query.add("select * from Dept where Code_='%s'", "191220");
        query.open();
        if (!query.eof()) {
            query.edit();
            query.setField("Name_", "rd-new");
            query.post();
//            query.delete();
        }

//        query.append();
//        query.setField("CorpCode_", "191220");
//        query.setField("Code_", "191220");
//        query.setField("Name_", "研发部");
//        query.setField("UpdateUser_", "admin");
//        query.setField("AppUser_", "admin");
//        query.setField("UpdateDate_", TDateTime.now());
//        query.setField("AppDate_", TDateTime.now());
//        query.post();
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
