package cn.cerc.mis.mail;

import cn.cerc.core.DataSet;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HtmlGridTest {
    @Test
    public void test_getDataSet() {
        DataSet ds = new DataSet();
        for (int i = 1; i < 3; i++)
            ds.append().setField("Code", "C00" + i).setField("Name", "N00" + i);
        String str = HtmlGrid.getDataSet(ds);
        assertTrue(!"".equals(str));
    }
}
