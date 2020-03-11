package cn.cerc.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RecordTest {
    private FieldDefs def = new FieldDefs();
    private Record item = new Record(def);

    @Test
    public void test_create() {
        assertEquals(def, item.getFieldDefs());
        assertEquals(item.getState(), DataSetState.dsNone);
    }

    @Test
    public void test_setState() {
        item.setState(DataSetState.dsInsert);
        assertEquals(item.getState(), DataSetState.dsInsert);
    }

    @Test
    public void test_setField() {
        String field1 = "code";
        String value1 = "value";
        item.setField(field1, value1);
        assertEquals(value1, item.getField(field1));

        String field2 = "num";
        Double value2 = 1.12345678;
        item.setField(field2, value2);
        assertEquals(value2, (double) item.getField(field2), 0);
    }

    @Test
    public void test_setField_error1() {
        Record obj = new Record();
        item.setField("object", obj);
        assertEquals(obj, item.getField("object"));
    }

    @Test(expected = RuntimeException.class)
    public void test_setField_error2() {
        item.setField(null, "value");
    }

    @Test
    public void test_getIn() {
        long value = System.currentTimeMillis();
        item.setField("value", value);
        assertEquals(value, (long) item.getDouble("value"));

        item.setField("value", "2.0");
        assertEquals(2, item.getInt("value"));
    }

    @Test
    public void test_getInteger() {
        item.setField("type", true);
        assertEquals(1, item.getDouble("type"), 0);
    }

    @Test
    public void test_setField_delta() {
        Record rs = new Record();
        rs.setField("Code_", "a");
        assertEquals(rs.getDelta().size(), 0);

        Object val = null;

        rs.setField("Code_", val);
        assertEquals(rs.getDelta().size(), 0);

        rs.setState(DataSetState.dsEdit);
        rs.setField("Code_", val);
        assertEquals(rs.getDelta().size(), 0);

        rs.setField("Code_", "c");
        rs.setField("Code_", "d");
        assertEquals(rs.getDelta().size(), 1);
        assertTrue(rs.getOldField("Code_") == val);
    }
}
