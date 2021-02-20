package cn.cerc.core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class TDateTimeTest {
    private String ym = "201512";
    private TDateTime obj;

    @Test
    public void test_getYearMonth() {
        obj = TDateTime.fromYearMonth(ym);
        String val = obj.getYearMonth();
        assertEquals("年月与日期互转", ym, val);
    }

    @Test
    public void test_incHour() {
        obj = TDateTime.fromYearMonth(ym).incHour(-1);
        assertThat("减1小时", obj.toString(), is("2015-11-30 23:00:00"));
        obj = TDateTime.fromYearMonth(ym).incHour(-25);
        assertThat("减25小时", obj.toString(), is("2015-11-29 23:00:00"));
        obj = TDateTime.fromYearMonth(ym).incHour(1);
        assertThat("加1小时", obj.getTime(), is("01:00:00"));
        obj = TDateTime.fromYearMonth(ym).incHour(12);
        assertThat("加12小时", obj.getTime(), is("12:00:00"));
    }

    @Test
    public void test_incMonth() {
        obj = TDateTime.fromYearMonth(ym).incMonth(-1);
        assertThat("取上月初", obj.getYearMonth(), is("201511"));

        obj = TDateTime.fromYearMonth("201503").incMonth(-1);
        assertThat("测试2月份", obj.getYearMonth(), is("201502"));
    }

    @Test
    public void test_monthEof() {
        obj = TDateTime.fromYearMonth(ym).monthEof();
        assertThat("取上月末", obj.getDate(), is("2015-12-31"));
    }

    @Test
    public void test_compareDay() {
        obj = TDateTime.now();
        assertSame(obj.compareDay(TDateTime.now().incDay(-1)), 1);
    }

    @Test
    public void test_FormatDateTime() {
        String val = TDateTime.FormatDateTime("YYMMDD", new TDateTime("2016-01-01"));
        assertEquals("160101", val);
    }
}
