package cn.cerc.db.queue;

import cn.cerc.db.core.ServerConfig;

/**
 * 阿里云消息队列
 */
public class QueueDB {

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

    private static String getQueueDB(String queue) {
        String queueDB = ServerConfig.getInstance().getProperty(queue);
        if (queueDB == null) {
            throw new RuntimeException(String.format("配置文件中未配置该key %s", queue));
        }
        return queueDB;
    }
}
