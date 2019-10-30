package cn.cerc.db.other;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.db.SampleData;
import cn.cerc.core.DataSet;

public class SumRecordTest {
    private SumRecord sum;
    private DataSet ds;

    @Before
    public void setUp() throws Exception {
        ds = SampleData.getDataSet();
        sum = new SumRecord(ds);
    }

    @Test
    public void testRun() {
        sum.addField("num");
        sum.run();
        assertEquals(45, sum.getDouble("num"), 0);
    }

}
