package cn.cerc.db.queue;

import cn.cerc.db.core.StubHandleText;
import org.junit.Before;
import org.junit.Test;

public class QueueQueryTest_receive {
    private QueueQuery dataSet;
    private StubHandleText handle;

    @Before
    public void setUp() {
        handle = new StubHandleText();
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
