package cn.cerc.db.queue;

import cn.cerc.core.ClassResource;
import cn.cerc.db.core.ServerConfig;

/**
 * 阿里云消息队列
 */
public class QueueDB {
    private static final ClassResource res = new ClassResource(QueueDB.class, "summer-db");

    /**
     * 系统消息
     **/
    public static final String MESSAGE = getQueueDB("queuedb.message");

    /**
     * 回算队列
     **/
    public static final String SUMMER = getQueueDB("queuedb.summer");

    /**
     * 资料同步
     **/
    public static final String MATERIAL = getQueueDB("queuedb.material");

    /**
     * 全文检索
     */
    public static final String ELASTICSEARCH = getQueueDB("ququedb.elasticsearch");

    private static String getQueueDB(String queue) {
        String queueDB = ServerConfig.getInstance().getProperty(queue);
        if (queueDB == null) {
            throw new RuntimeException(String.format(res.getString(1, "配置文件中未配置该key %s"), queue));
        }
        return queueDB;
    }
}
