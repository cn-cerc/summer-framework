package cn.cerc.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataSetTest2 {
    private DataSet ds = new DataSet();;
    private static final int MAX = 10000;

    @Test
    public void test_append() {
        ds.append();
        ds.setField("code", "value");
        ds.post();
        assertEquals(1, ds.size());
    }

    @Before
    public void setUp() throws Exception {
        ds = new DataSet();
    }

    @After
    public void tearDown() throws Exception {
        ds.close();
    }

    @Test(timeout = 10000)
    public void testLocate_1_old() {
        for (int i = 0; i < MAX; i++) {
            String key = "code" + i;
            ds.append();
            ds.setField("code", key);
            ds.setField("value", i);
            ds.post();
        }
        for (int i = 100; i < MAX; i++)
            assertTrue("查找错误", ds.locate("value", i));
    }

    @Test(timeout = 50000)
    public void testLocate_2_new() {
        for (int i = 0; i < MAX; i++) {
            String key = "code" + i;
            ds.append();
            ds.setField("code", key);
            ds.setField("value", i);
            ds.post();
        }
        for (int i = 100; i < MAX; i++) {
            assertTrue(ds.lookup("value", i) != null);
        }
        ds.append();
        ds.setField("code", "codexx");
        ds.setField("value", "xx");
        ds.post();
        assertTrue(ds.lookup("value", "xx") != null);

        Record record = ds.lookup("value", "xx");
        record.setField("code", "value");
        assertEquals(ds.getString("code"), "value");
        System.out.println(ds.getRecNo());

        ds.setField("code", "value2");
        assertEquals(record.getString("code"), "value2");

        assertTrue(ds.lookup("value", "xx") != null);
    }

    public static void main(String[] args) {
        Map<String, Object> values = new HashMap<>();
        values.put("code", "1");
        values.put("code", "2");
        System.out.println(values.keySet().spliterator());
    }
}
