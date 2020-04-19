package cn.cerc.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RecordTest_JSON {
    private FieldDefs def = new FieldDefs();
    private Record item = new Record(def);
    private String jsonStr = "{\"Boolean\":true," + "\"Date\":\"2016-06-20 00:00:00\","
            + "\"DateTime\":\"2016-06-20 09:26:35\"," + "\"Double\":3.12," + "\"Integer\":123," + "\"Null\":null,"
            + "\"OldDate\":\"2016-06-20 09:26:35\"," + "\"String\":\"AAA\"}";

    @Test
    public void test_toJSON_old() {
        item.setField("String", "AAA");
        item.setField("Double", 3.12);
        item.setField("Integer", 123);
        item.setField("OldDate", "2016-06-20 09:26:35");
        item.setField("Date", "2016-06-20 00:00:00");
        item.setField("DateTime", "2016-06-20 09:26:35");
        item.setField("Boolean", true);
        item.setField("Null", null);
        assertEquals(jsonStr, item.toString());
    }

    @Test
    public void test_fromJSON_old() {
        item.setJSON(jsonStr);
        System.out.println(item.getField("String"));
        assertEquals("AAA", item.getString("String"));
        assertEquals(jsonStr, item.toString());
    }
}
