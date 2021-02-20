package cn.cerc.db.mysql;

import cn.cerc.db.core.StubHandleText;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqlQueryTest_save {
    private StubHandleText handle;
    private MysqlConnection conn;

    @Before
    public void setUp() {
        handle = new StubHandleText();
        conn = (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
    }

    @Test
    @Ignore
    public void test_delete() {
        conn.execute("delete from temp");
        SqlQuery ds = new SqlQuery(handle);
        ds.setBatchSave(true);
        System.out.println("before insert, record count: " + getTotal("temp"));

        ds.add("select * from temp");
        ds.open();

        ds.append();
        ds.setField("Code_", "codeA");
        ds.setField("Name_", "name");
        ds.setField("Value_", 1);
        ds.post();

        ds.edit();
        ds.setField("Value_", 2);
        ds.post();
        System.out.println("after insert, record count: " + getTotal("temp"));

        while (ds.fetch())
            ds.delete();
        assertEquals(ds.size(), 0);

        if (ds.isBatchSave()) {
            System.out.println("before save, record count: " + getTotal("temp"));
            ds.save();
            System.out.println("after save, record count: " + getTotal("temp"));
        }
    }

    @Test
    @Ignore
    public void test_insert() {
        conn.execute("delete from temp");
        System.out.println("batchSave is true");
        insertTest(true);
        conn.execute("delete from temp");
        System.out.println("batchSave is false");
        insertTest(false);
    }

    private void insertTest(boolean batchSave) {
        SqlQuery ds = new SqlQuery(handle);
        ds.setBatchSave(batchSave);
        ds.add("select * from temp");
        ds.open();

        ds.append();
        ds.setField("Code_", "codeA");
        ds.setField("Name_", "name");
        ds.setField("Value_", 1);
        ds.post();

        ds.edit();
        ds.setField("Value_", 2);
        ds.post();

        if (batchSave)
            ds.save();

        ds.setBatchSave(false);
        ds.append();
        ds.setField("Code_", "codeB");
        ds.setField("Name_", "name");
        ds.setField("Value_", 3);
        ds.post();
    }

    private int getTotal(String table) {
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select count(*) as total from %s", table);
        ds.open();
        return ds.getInt("total");
    }
}
