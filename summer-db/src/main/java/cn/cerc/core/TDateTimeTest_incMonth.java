package cn.cerc.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TDateTimeTest_incMonth {

    @Test
    public void test_incMonth1() {
        TDateTime obj = TDateTime.fromYearMonth("201601");
        for (int i = 1; i < 365; i++) {
            check(obj.toString(), i);
        }
        for (int i = 365; i >= 0; i--) {
            check(obj.toString(), i);
        }
    }

    public void check(String base, int offset) {
        TDateTime obj = new TDateTime(base);
        TDateTime val = obj.incMonth(offset);
        int v1 = Integer.parseInt(val.getYearMonth().substring(0, 4)) * 12
                + Integer.parseInt(val.getYearMonth().substring(4, 6));
        int v2 = Integer.parseInt(obj.getYearMonth().substring(0, 4)) * 12
                + Integer.parseInt(obj.getYearMonth().substring(4, 6));
        assertEquals(v1 - v2, offset);
    }
}
