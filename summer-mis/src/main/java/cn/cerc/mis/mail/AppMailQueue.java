package cn.cerc.mis.mail;

@Deprecated
//TODO 此处应该改使用ClassConfig
public class AppMailQueue {
    // 每晚自动邮件
    public static final String queueAutoMail = "automail";
    // 业务手动邮件
    public static final String queueSendMail = "sendmail";
    // 测试队列
    public static final String queueTest = "test";
}
