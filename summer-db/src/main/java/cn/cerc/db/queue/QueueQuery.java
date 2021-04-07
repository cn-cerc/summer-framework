package cn.cerc.db.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.google.gson.JsonSyntaxException;

import cn.cerc.core.ClassResource;
import cn.cerc.core.ISession;
import cn.cerc.db.SummerDB;
import cn.cerc.db.core.DataQuery;
import cn.cerc.db.core.ISessionOwner;

public class QueueQuery extends DataQuery {
    private static final ClassResource res = new ClassResource(QueueQuery.class, SummerDB.ID);
    private static final Logger log = LoggerFactory.getLogger(QueueQuery.class);

    private static final long serialVersionUID = 7781788221337787366L;
    private QueueOperator operator;
    private String queueCode;
    private AliyunQueueConnection connection;
    private CloudQueue queue;
    private String receiptHandle;
    private QueueMode queueMode = QueueMode.append;

    public QueueQuery(ISession session) {
        super(session);
        this.setBatchSave(true);
        this.connection = (AliyunQueueConnection) session.getProperty(AliyunQueueConnection.sessionId);
    }

    public QueueQuery(ISessionOwner owner) {
        this(owner.getSession());
    }

    @Override
    public DataQuery open() {
        if (queueCode == null) {
            queueCode = getOperator().findTableName(this.getSqlText().getText());
            queue = connection.openQueue(queueCode);
        }
        if (null == queueCode || "".equals(queueCode)) {
            throw new RuntimeException("queueCode is null");
        }
        if (this.active) {
            throw new RuntimeException("active is true");
        }

        // 当maximum设置为1时，读取消息
        if (this.queueMode == QueueMode.recevie) {
            Message message = connection.receive(queue);
            if (message != null) {
                try {
                    this.setJSON(message.getMessageBody());
                    receiptHandle = message.getReceiptHandle();
                    this.setActive(true);
                } catch (JsonSyntaxException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return this;
    }

    @Override
    public void save() {
        if (this.queueMode != QueueMode.append) {
            throw new RuntimeException(res.getString(1, "当前作业模式下，不允许保存"));
        }
        connection.append(queue, getJSON());
        log.info("message save success");
    }

    /**
     * @return 移除消息队列
     */
    public boolean remove() {
        if (receiptHandle == null) {
            return false;
        }
        connection.delete(queue, receiptHandle);
        receiptHandle = null;
        return true;
    }

    /**
     * 创建消息队列
     *
     * @param queueCode 队列代码
     * @return 返回创建的队列
     */
    public CloudQueue create(String queueCode) {
        return connection.createQueue(queueCode);
    }

    // 判断消息队列是否存在
    public boolean isExistQueue() {
        return queue.isQueueExist();
    }

    @Override
    public QueueOperator getOperator() {
        if (operator == null) {
            operator = new QueueOperator();
        }
        return operator;
    }

    @Override
    public final void setBatchSave(boolean batchSave) {
        super.setBatchSave(batchSave);
        if (!batchSave) {
            throw new RuntimeException("QueueQuery.batchSave can not be false");
        }
    }

    public QueueMode getQueueMode() {
        return queueMode;
    }

    public void setQueueMode(QueueMode queueMode) {
        this.queueMode = queueMode;
    }

    @Override
    public QueueQuery add(String sql) {
        super.add(sql);
        return this;
    }

    @Override
    public QueueQuery add(String format, Object... args) {
        super.add(format, args);
        return this;
    }

}
