package cn.cerc.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DataSetTest_JSON {
    private DataSet ds = new DataSet();
    private String jsonStr = "{\"head\":{\"It\":1,\"TBNo\":\"OD001\"},"
            + "\"dataset\":[[\"It\",\"Part\",\"Desc\"],[1,\"001\",\"desc\"],[2,\"001\",\"desc\"]]}";

    @Test
    public void test_toJSON() {
        Record head = ds.getHead();
        head.setField("TBNo", "OD001");
        head.setField("It", 1);
        ds.append();
        ds.setField("It", 1);
        ds.setField("Part", "001");
        ds.setField("Desc", "desc");
        ds.append();
        ds.setField("It", 2);
        ds.setField("Part", "001");
        ds.setField("Desc", "desc");
        assertEquals(jsonStr, ds.getJSON());
    }

    @Test
    public void test_fromJSON() {
        ds.setJSON(jsonStr);
        assertEquals(jsonStr, ds.getJSON());
        assertEquals("1", ds.getHead().getString("It"));
    }
}
