package cn.cerc.db.queue;

/**
 * 阿里云消息队列
 */
public class QueueDB {

    /** 自动发送邮件 **/
    public static final String AUTOMAIL = "automail";

    /** 任务队列 **/
    public static final String JOBLIST = "joblist";

    /** 系统消息 **/
    public static final String MESSAGE = "message";

    /** 系统消息 测试队列 **/
    public static final String MESSAGE_TEST = "message-test";

    /** 邮件发送 **/
    public static final String SENDMAIL = "sendmail";

    /** 回算队列 **/
    public static final String SUMMER = "summer";

    /** 资料同步 **/
    public static final String MATERIAL = "material";

    /** 资料同步 测试队列 **/
    public static final String MATERIAL_TEST = "material-test";

    /** 测试队列 **/
    public static final String TEST = "test";

    /** 全文检索 */
    public static final String ELASTICSEARCH = "elasticsearch";

    /** 全文检索 测试队列 **/
    public static final String ELASTICSEARCH_TEST = "elasticsearch-test";
}
