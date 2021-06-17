package cn.cerc.db.other;

import cn.cerc.core.DataSet;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CountRecordTest {
    private DataSet ds;

    @Before
    public void setUp() throws Exception {
        ds = new DataSet();
        ds.append();
        ds.setField("a", 1);
        ds.append();
        ds.setField("a", 2);
        ds.append();
        ds.setField("a", 3);
        ds.append();
        ds.setField("a", 4);
    }

    @Test
    public void test() {
        CountRecord count = new CountRecord(ds).run(rs -> rs.getInt("a") < 2 ? "t" : "f");
        assertEquals(count.getCount("t"), 1);
        assertEquals(count.getCount("f"), 3);
        assertEquals(count.getCount("b"), 0);
        for (String group : count.getGroups()) {
            System.out.println(String.format("group %s: %s", group, count.getCount(group)));
        }
    }
}
