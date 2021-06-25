package cn.cerc.db.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.google.gson.JsonSyntaxException;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.core.SqlText;
import cn.cerc.db.SummerDB;
import cn.cerc.db.core.IHandle;

public class QueueQuery extends DataSet implements IHandle {
    private static final ClassResource res = new ClassResource(QueueQuery.class, SummerDB.ID);
    private static final Logger log = LoggerFactory.getLogger(QueueQuery.class);
    private static final long serialVersionUID = 7781788221337787366L;
    private QueueOperator operator;
    private String queueCode;
    private QueueServer connection;
    private CloudQueue queue;
    private String receiptHandle;
    private QueueMode queueMode = QueueMode.append;
    private SqlText sqlText = new SqlText();
    private boolean active;
    private ISession session;

    public QueueQuery(IHandle handle) {
        super();
        this.session = handle.getSession();
        this.connection = (QueueServer) getSession().getProperty(QueueServer.SessionId);
    }

    public QueueQuery open() {
        if (queueCode == null) {
            queueCode = SqlText.findTableName(this.getSqlText().getText());
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

    public void save() {
        if (this.queueMode != QueueMode.append) {
            throw new RuntimeException(res.getString(1, "当前作业模式下，不允许保存"));
        }
        connection.append(queue, getJSON());
        log.debug("message save success");
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

    public QueueOperator getOperator() {
        if (operator == null) {
            operator = new QueueOperator();
        }
        return operator;
    }

    public QueueMode getQueueMode() {
        return queueMode;
    }

    public void setQueueMode(QueueMode queueMode) {
        this.queueMode = queueMode;
    }

    public QueueQuery add(String sql) {
        sqlText.add(sql);
        return this;
    }

    public QueueQuery add(String format, Object... args) {
        sqlText.add(format, args);
        return this;
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SqlText getSqlText() {
        return sqlText;
    }

}
