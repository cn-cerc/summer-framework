package cn.cerc.db.mssql;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubHandleText;
import org.junit.Before;
import org.junit.Test;

public class MssqlQueryTest {

    private IHandle handle;

    @Before
    public void setUP() {
        handle = new StubHandleText();
    }

    @Test
    public void test_append() {
        MssqlQuery query = new MssqlQuery(handle);
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

}
