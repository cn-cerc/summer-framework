package cn.cerc.mis.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import cn.cerc.core.TDateTime;

public class QueueConnectionTest {

    public static void main(String[] args) {
        QueueConnection conn;
        try {
            conn = new QueueConnection();
        } catch (JMSException e1) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Session session = conn.getConnectio().createSession(true, Session.SESSION_TRANSACTED);
                        for (int i = 0; i < 3; i++) {
                            // 创建一个消息队列
                            Queue queue = session.createQueue("activeMQ");
                            // 消息生产者
                            MessageProducer messageProducer = session.createProducer(queue);
                            // 创建一条消息
                            TextMessage msg = session.createTextMessage("curTime:" + TDateTime.Now());
                            System.out.println(Thread.currentThread().getName() + " send:" + msg.getText());
                            // 发送消息
                            messageProducer.send(msg);
                            // 提交事务
                            session.commit();

                            Thread.sleep(1000);
                        }
                    } catch (JMSException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Session session = conn.getConnectio().createSession(true, Session.SESSION_TRANSACTED);
                    Queue queue = session.createQueue("activeMQ");
                    MessageConsumer consumer = session.createConsumer(queue);
                    while (true) {
                        Message msg = consumer.receive();
                        if (msg != null && msg instanceof TextMessage) {
                            System.out.println(
                                    Thread.currentThread().getName() + " receive:" + ((TextMessage) msg).getText());
                             msg.acknowledge();
                        } else {
                            System.out.println("receive is null");
                            break;
                        }
                        Thread.sleep(100);
                    }
                    consumer.close();
                } catch (JMSException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
