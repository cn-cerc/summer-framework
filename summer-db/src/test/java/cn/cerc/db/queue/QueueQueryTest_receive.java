package cn.cerc.db.queue;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.core.ISession;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.StubSession;

public class QueueQueryTest_receive implements IHandle {
    private QueueQuery dataSet;
    private ISession session;

    @Before
    public void setUp() {
        session = new StubSession();
        dataSet = new QueueQuery(this);
    }

    @Test
    public void test() {
        dataSet.setQueueMode(QueueMode.recevie);
        dataSet.add("select * from test");
        dataSet.open();

        System.out.println(dataSet.getActive());
        System.out.println(dataSet.getJSON());
        // do something
        dataSet.remove();
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
