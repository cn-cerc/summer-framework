package cn.cerc.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 专用于测试toList支持
 * 
 * @author 张弓
 *
 */
public class DataSetTest_List {
    private DataSet ds;

    @Before
    public void setUp() {
        ds = new DataSet();
        ds.append();
        ds.setField("A", "A01");
        ds.setField("B", "B01");
        ds.append();
        ds.setField("A", "A02");
        ds.setField("B", "B02");
        ds.append();
        ds.setField("A", "A03");
        ds.setField("B", "B03");
    }

    @Test
    public void test() {
        int i = 0;
        for (@SuppressWarnings("unused")
        Record record : ds) {
            i++;
        }
        assertEquals(i, ds.size());
    }

}
