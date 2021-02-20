package cn.cerc.db.mysql;

import cn.cerc.core.Record;
import cn.cerc.db.core.StubHandleText;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OperatorTest {
    private int maxTest = 50;

    private StubHandleText handle;

    @Before
    public void setUp() {
        handle = new StubHandleText();
        new SqlOperator(handle);
    }

    @Test
    @Ignore
    public void test_2_insert_new() {
        MysqlConnection conn = (MysqlConnection) handle.getProperty(MysqlConnection.sessionId);
        conn.execute("delete from temp where name_='new'");
        SqlQuery ds = new SqlQuery(handle);
        ds.getSqlText().setMaximum(0);
        ds.add("select * from temp");
        ds.open();
        for (int i = 0; i < maxTest; i++) {
            ds.append();
            ds.setField("Code_", "new" + i);
            ds.setField("Name_", "new");
            ds.setField("Value_", i + 1);
            ds.post();
        }
    }

    @Test
    @Ignore
    public void test_3_insert_new() {
        SqlOperator obj = new SqlOperator(handle);
        obj.setTableName("temp");
        for (int i = 0; i < maxTest; i++) {
            Record record = new Record();
            record.getFieldDefs().add("UID_");
            record.setField("Code_", "code1");
            record.setField("Name_", "new");
            record.setField("Value_", i + 1);
            obj.insert(record);
        }
    }

    @Test
    @Ignore
    public void test_4_update_new() {
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select * from temp");
        ds.open();
        while (ds.fetch()) {
            ds.edit();
            ds.setField("Code_", ds.getString("Code_") + "a");
            ds.setField("Value_", ds.getDouble("Value_") + 1);
            ds.post();
        }
    }

    @Test
    @Ignore
    public void test_6_delete_new() {
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select * from temp where Name_='new'");
        ds.open();
        while (!ds.eof())
            ds.delete();
    }

    @Test
    @Ignore
    public void test_findTableName() {
        String sql = "select * from Dept";
        assertEquals(SqlOperator.findTableName(sql), "Dept");
        sql = "select * from \r\n Dept";
        assertEquals(SqlOperator.findTableName(sql), "Dept");
        sql = "select * from \r\nDept";
        assertEquals(SqlOperator.findTableName(sql), "Dept");
        sql = "select * from\r\n Dept";
        assertEquals(SqlOperator.findTableName(sql), "Dept");
        sql = "select * FROM Dept";
        assertEquals(SqlOperator.findTableName(sql), "Dept");
    }
}
