package cn.cerc.db.mysql;

import cn.cerc.core.PostFieldException;
import cn.cerc.core.TDateTime;
import cn.cerc.db.core.StubHandleText;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SqlQueryTest_post1 {
    private SqlQuery ds;
    private StubHandleText conn;

    @Before
    public void setUp() {
        conn = new StubHandleText();
        ds = new SqlQuery(conn);
    }

    @Test(expected = PostFieldException.class)
    @Ignore(value = "仅允许在测试数据库运行")
    public void post_error() {
        ds.getFieldDefs().add("Test");
        ds.add("select * from Dept where CorpNo_='%s'", "144001");
        ds.open();
        ds.edit();
        ds.setField("updateDate_", TDateTime.Now().incDay(-1));
        ds.post();
    }

    @Test()
    @Ignore(value = "仅允许在测试数据库运行")
    public void post() {
        ds.add("select * from Dept where CorpNo_='%s'", "144001");
        ds.open();
        ds.setOnBeforePost(ds -> {
            System.out.println("before post");
        });
        ds.edit();
        ds.setField("Test", "aOK");
        ds.setField("UpdateDate_", TDateTime.Now().incDay(-1));
        ds.post();
    }
}
