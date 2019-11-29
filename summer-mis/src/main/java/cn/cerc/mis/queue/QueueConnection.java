package cn.cerc.mis.queue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueConnection {
    // ActiveMq 的默认用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    // ActiveMq 的默认登录密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    // ActiveMQ 的链接地址
    private static final String BROKEN_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    // 链接工厂
    private ConnectionFactory connectionFactory;
    // 链接对象
    private Connection connection;
    private Session session;

    public QueueConnection() throws JMSException {
        // 创建一个链接工厂
        connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEN_URL);
        // 从工厂中创建一个链接
        connection = connectionFactory.createConnection();
        // 开启链接
        connection.start();
        session = connection.createSession(true, Session.SESSION_TRANSACTED);
    }

    public Connection getConnectio() {
        return connection;
    }

    /**
     * 发送消息
     * 
     * @param disname
     *            消息队列id
     * @param data
     *            要发送的数据
     * @throws JMSException
     */
    public void sendMessage(String disname, String data) throws JMSException {
        // 创建一个消息队列
        Queue queue = session.createQueue(disname);
        // 消息生产者
        MessageProducer messageProducer = session.createProducer(queue);
        // 创建一条消息
        TextMessage msg = session.createTextMessage(data);
        System.out.println(Thread.currentThread().getName() + " send:" + msg.getText());
        // 发送消息
        messageProducer.send(msg);
        // 提交事务
        session.commit();
    }

    /**
     * 取得消息
     * 
     * @param disname
     *            消息队列id
     * @return 返回取得的消息
     * @throws JMSException
     */
    public Message receiveMessage(String disname) throws JMSException {
        Queue queue = session.createQueue(disname);
        MessageConsumer consumer = session.createConsumer(queue);
        Message msg = consumer.receive();
        if (msg != null && msg instanceof TextMessage)
            System.out.println(Thread.currentThread().getName() + " receive:" + ((TextMessage) msg).getText());
        consumer.close();
        return null;
    }
}
