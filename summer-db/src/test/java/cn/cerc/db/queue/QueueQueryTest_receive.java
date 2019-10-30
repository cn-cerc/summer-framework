package cn.cerc.db.queue;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.db.core.StubHandle;

public class QueueQueryTest_receive {
    private QueueQuery dataSet;
    private StubHandle handle;

    @Before
    public void setUp() {
        handle = new StubHandle();
        dataSet = new QueueQuery(handle);
    }

    @Test
    public void test() {
        dataSet.setQueueMode(QueueMode.recevie);
        dataSet.add("select * from %s", QueueDB.TEST);
        dataSet.open();

        System.out.println(dataSet.getActive());
        System.out.println(dataSet.getJSON());
        // do something
        dataSet.remove();
    }
}
