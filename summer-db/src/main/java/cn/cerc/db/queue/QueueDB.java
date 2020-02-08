package cn.cerc.db.queue;

import cn.cerc.db.core.ServerConfig;

/**
 * 阿里云消息队列
 */
public class QueueDB {

    /**
     * 自动发送邮件
     **/
    public static final String AUTOMAIL = getQueueDB("ququedb.automail");

    /**
     * 任务队列
     **/
    public static final String JOBLIST = getQueueDB("ququedb.joblist");

    /**
     * 系统消息
     **/
    public static final String MESSAGE = getQueueDB("ququedb.message") ;

    /**
     * 邮件发送
     **/
    public static final String SENDMAIL = getQueueDB("ququedb.sendmail");

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

    /**
     * 全文检索
     */
    public static final String ELASTICSEARCH = getQueueDB("ququedb.elasticsearch");

    private static String getQueueDB(String queue) {
        String queueDB = ServerConfig.getInstance().getProperty(queue);
        if (queueDB == null) {
            throw new RuntimeException(String.format("配置文件中未配置该key %s", queue));
        }
        return queueDB;
    }
}
