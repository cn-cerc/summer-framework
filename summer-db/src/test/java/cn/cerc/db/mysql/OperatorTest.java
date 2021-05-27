package cn.cerc.db.mysql;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class OperatorTest implements IHandle {
    private int maxTest = 50;

    private ISession session;

    @Before
    public void setUp() {
        session = new StubSession();
        new SqlOperator(this);
    }

    @Test
    @Ignore
    public void test_2_insert_new() {
        MysqlServerMaster conn = this.getMysql();
        conn.execute("delete from temp where name_='new'");
        SqlQuery ds = new SqlQuery(this);
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
        SqlOperator obj = new SqlOperator(this);
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
        SqlQuery ds = new SqlQuery(this);
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
        SqlQuery ds = new SqlQuery(this);
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

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }
}
