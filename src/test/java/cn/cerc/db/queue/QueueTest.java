package cn.cerc.db.queue;

import org.junit.Test;

public class QueueTest {

    @Test
    public void test() {
        Queue queue = QueueFactory.getQueue("test");
        // for (int i = 1; i < 4; i++)
        // queue.append("val" + i);
        while (queue.read() != null) {
            System.out.println(queue.getBodyText());
            System.out.println(queue.getMessage().getNextVisibleTime());
            queue.delete();
        }
    }

}
