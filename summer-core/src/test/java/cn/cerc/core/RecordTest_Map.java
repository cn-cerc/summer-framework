package cn.cerc.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 专用于测试Map支持
 * 
 * @author 张弓
 *
 */
public class RecordTest_Map {
    private Record rs;

    @Before
    public void setUp() {
        rs = new Record();
        rs.setField("A", "A001");
        rs.setField("B", "B001");
        rs.setField("C", "C001");
    }

    @Test
    public void test() {
        int i = 0;
        for (@SuppressWarnings("unused")
        String key : rs.getItems().keySet()) {
            i++;
        }
        assertEquals(i, rs.size());
    }
}
