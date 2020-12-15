package cn.cerc.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TDateTest {

    @Test
    public void test_Today() {
        TDate obj = TDate.today();
        assertEquals(obj.getDate(), TDateTime.now().getDate());
    }
}
