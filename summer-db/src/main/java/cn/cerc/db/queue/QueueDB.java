package cn.cerc.db.queue;

import cn.cerc.db.core.ServerConfig;

/**
 * 阿里云消息队列
 */
public class QueueDB {

    /**
     * 系统消息
     **/
    public static final String MESSAGE = "message";

    /**
     * 回算队列
     **/
    public static final String SUMMER = getQueueDB("ququedb.summer");

    /**
     * 资料同步
     **/
    public static final String MATERIAL = getQueueDB("ququedb.material");

    /**
     * 测试队列
     **/
    public static final String TEST = "test";

    private static String getQueueDB(String queue) {
        String queueDB = ServerConfig.INSTANCE.getProperty(queue);
        if (queueDB == null) {
            throw new RuntimeException(String.format("配置文件中未配置该key %s", queue));
        }
        return queueDB;
    }
}
