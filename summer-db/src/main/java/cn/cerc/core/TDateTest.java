package cn.cerc.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TDateTest {

    @Test
    public void test_Today() {
        TDate obj = TDate.Today();
        assertEquals(obj.getDate(), TDateTime.Now().getDate());
    }
}
