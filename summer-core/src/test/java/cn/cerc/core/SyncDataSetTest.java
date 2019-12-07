package cn.cerc.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SyncDataSetTest {
    private DataSet src = new DataSet();
    private DataSet tar = new DataSet();

    @Before
    public void setUp() throws Exception {
        src.append();
        src.setField("code", "a");
        src.append();
        src.setField("code", "b");
        src.append();
        src.setField("code", "c");

        tar.append();
        tar.setField("code", "a");
        tar.append();
        tar.setField("code", "c");
        tar.append();
        tar.setField("code", "d");
    }

    @Test
    public void test() throws SyncUpdateException {
        SyncDataSet sds = new SyncDataSet(src, tar, "code");
        
        int total = sds.execute(new ISyncDataSet(){
            @Override
            public void process(Record src, Record tar) throws SyncUpdateException {
                if (tar == null)
                    System.out.println("insert record: " + src.getField("code"));
                else if (src == null)
                    System.out.println("delete record: " + tar.getField("code"));
                else
                    System.out.println("update record: " + src.getField("code"));
            }
        });
        assertEquals(total, 4);
    }

}
