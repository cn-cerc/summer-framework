package cn.cerc.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataSetTest_delete {
    private DataSet ds = new DataSet();

    @Before
    public void setUp() throws Exception {
        ds.append().setField("code", "a");
        ds.append().setField("code", "b");
        ds.append().setField("code", "c");
    }

    @Test
    public void test_a() {
        int i = 0;
        ds.first();
        while (!ds.eof()) {
            i++;
            if ("a".equals(ds.getString("code"))) {
                ds.delete();
            } else {
                ds.next();
            }
        }
        assertEquals(i, 3);
    }

    @Test
    public void test_b() {
        int i = 0;
        ds.first();
        while (!ds.eof()) {
            i++;
            if ("b".equals(ds.getString("code"))) {
                ds.delete();
            } else {
                ds.next();
            }
        }
        assertEquals(i, 3);
    }

    @Test
    public void test_c() {
        int i = 0;
        ds.first();
        while (!ds.eof()) {
            i++;
            if ("c".equals(ds.getString("code"))) {
                ds.delete();
            } else {
                ds.next();
            }
        }
        assertEquals(i, 3);
    }
}
