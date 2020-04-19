package cn.cerc.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TDateTest {

    @Test
    public void test_Today() {
        TDate obj = TDate.Today();
        assertEquals(obj.getDate(), TDateTime.Now().getDate());
    }
}
