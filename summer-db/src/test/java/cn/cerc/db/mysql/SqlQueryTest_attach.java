package cn.cerc.db.mysql;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.core.Record;
import cn.cerc.db.core.StubHandle;

public class SqlQueryTest_attach {
    private SqlQuery ds;
    private StubHandle handle;

    @Before
    public void setUp() {
        handle = new StubHandle();
        ds = new SqlQuery(handle);
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

}
