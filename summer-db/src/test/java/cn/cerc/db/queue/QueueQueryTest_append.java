package cn.cerc.db.queue;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.db.core.StubHandle;

public class QueueQueryTest_append {
    private QueueQuery dataSet;
    private StubHandle handle;

    @Before
    public void setUp() {
        handle = new StubHandle();
        dataSet = new QueueQuery(handle);
    }

    @Test
    public void test() {
        // 增加模式
        dataSet.setQueueMode(QueueMode.append);
        dataSet.add("select * from %s", QueueDB.TEST);
        dataSet.open();
        System.out.println(dataSet.getActive());

        // append head
        dataSet.getHead().setField("queueHeadData1", "queueHeadData1");
        dataSet.getHead().setField("queueHeadData2", "queueHeadData2");
        dataSet.getHead().setField("queueHeadData3", "queueHeadData3");
        dataSet.getHead().setField("queueHeadData4", "queueHeadData4");

        // append body
        dataSet.append();
        dataSet.setField("queueBodyData1", "queueBodyData1");
        dataSet.setField("queueBodyData2", "queueBodyData2");
        dataSet.setField("queueBodyData3", "queueBodyData3");
        dataSet.setField("queueBodyData4", "queueBodyData4");
        dataSet.setField("queueBodyData5", "queueBodyData5");

        dataSet.save();
    }
}
