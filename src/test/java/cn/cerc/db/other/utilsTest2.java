package cn.cerc.db.other;

import cn.cerc.core.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class utilsTest2 {
    private double value;
    private double expecked;

    public utilsTest2(double value, double expecked) {
        this.value = value;
        this.expecked = expecked;
    }

    // 以下代码请勿删除，此是用于大量数据批次测试的范本
    @Parameters
    public static Collection<Object[]> init() {
        Object[][] objects = {{1.234, 1.23}, {1.235, 1.24}, {1.245, 1.25}};
        return Arrays.asList(objects);
    }

    @Test
    public void testRoundTo() {
        double val = Utils.roundTo(value, -2);
        assertEquals(val, is(expecked));
    }
}
