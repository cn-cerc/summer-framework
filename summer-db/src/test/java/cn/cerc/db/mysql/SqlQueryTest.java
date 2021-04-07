package cn.cerc.db.mysql;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.db.core.StubHandleText;

public class SqlQueryTest {
    private static final Logger log = LoggerFactory.getLogger(SqlQueryTest.class);

    private String userInfo;
    private SqlQuery ds;
    private StubHandleText handle;

    @Before
    public void setUp() {
        handle = new StubHandleText();
        ds = new SqlQuery(handle);
        userInfo = "Account";
    }

    @Test
    public void test_open() {
        ds.getSqlText().setMaximum(1);
        ds.add("select Code_,Name_ from %s", userInfo);
        ds.open();
        assertEquals(ds.size(), 1);
    }

    @Test
    public void test_open_getSelectCommand() {
        String sql;
        sql = String.format("select Code_,Name_ from %s", userInfo);
        ds.add(sql);
        assertEquals(String.format("%s limit %s", sql, (BigdataException.MAX_RECORDS + 2)),
                ds.getSqlText().getCommand());

        ds.clear();
        sql = String.format("select Code_,Name_ from %s limit 1", userInfo);
        ds.getSqlText().setMaximum(-1);
        ds.add(sql);
        assertEquals(sql, ds.getSqlText().getCommand());

        ds.clear();
        ds.getSqlText().setMaximum(BigdataException.MAX_RECORDS);
        sql = String.format("select Code_,Name_ from %s", userInfo);
        ds.add(sql);

        assertEquals(String.format("%s limit %s", sql, (BigdataException.MAX_RECORDS + 2)),
                ds.getSqlText().getCommand());
    }

    @Test
    public void test_open_2() {
        ds.getSqlText().setMaximum(1);
        String str = "\\小王233\\";
        ds.add("select * from %s where Name_='%s'", userInfo, str);
        log.info(ds.getSqlText().getText());
        ds.open();

        ds.clear();
        str = "\\\\小王233\\\\";
        ds.add("select * from %s where Name_='%s'", userInfo, str);
        log.info(ds.getSqlText().getText());
        ds.open();

    }

}
