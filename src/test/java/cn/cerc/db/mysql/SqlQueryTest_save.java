package cn.cerc.db.mysql;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class SqlQueryTest_save implements IHandle {
    private ISession session;
    private MysqlServerMaster conn;

    @Before
    public void setUp() {
        session = new StubSession();
        conn = this.getMysql();
    }

    @Test
    @Ignore
    public void test_delete() {
        conn.execute("delete from temp");
        MysqlQuery ds = new MysqlQuery(this);
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
        MysqlQuery ds = new MysqlQuery(this);
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
        MysqlQuery ds = new MysqlQuery(this);
        ds.add("select count(*) as total from %s", table);
        ds.open();
        return ds.getInt("total");
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
