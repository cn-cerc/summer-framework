package cn.cerc.db.queue;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class QueueQueryTest_append implements IHandle {
    private QueueQuery dataSet;
    private ISession session;

    @Before
    public void setUp() {
        session = new StubSession();
        dataSet = new QueueQuery(this);
    }

    @Test
    public void test() {
        // 增加模式
        dataSet.setQueueMode(QueueMode.append);
        dataSet.add("select * from test");
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

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }
}
