package cn.cerc.mis.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;

public class SmtpServer {
    private static final Logger log = LoggerFactory.getLogger(SmtpServer.class);
    private static final ClassConfig config = new ClassConfig(SmtpServer.class, null);
    // 发送服务器
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    // 发送服务器port号，可不设置，默认为 465
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    // 是否开启调试模式，可不设置，取值为 true / false，默认为 false
    public static final String MAIL_SMTP_DEBUG = "mail.smtp.debug";
    // 发送者邮箱帐号
    public static final String MAIL_ACCOUNT = "mail.from.account";
    // 发送者邮箱密码
    public static final String MAIL_PASSWORD = "mail.from.password";
    // 发送者别名，可不设置
    public static final String MAIL_ALIAS = "mail.from.alias";

    private final Properties properties;

    public SmtpServer() {
        this(config.getProperties());
    }

    public SmtpServer(Properties mailConfig) {
        properties = new Properties(mailConfig);

        String account = properties.getProperty(MAIL_ACCOUNT);
        if (Utils.isEmpty(account)) {
            throw new RuntimeException("发件人邮箱地址未配置");
        }

        String smtpHost = properties.getProperty(MAIL_SMTP_HOST);
        if (Utils.isEmpty(properties.getProperty(MAIL_SMTP_HOST))) {
            throw new RuntimeException("发件人服务地址未配置");
        }

        if (Utils.isEmpty(properties.getProperty(MAIL_PASSWORD))) {
            throw new RuntimeException("发件人邮箱密码未配置");
        }
        if (Utils.isEmpty(properties.getProperty(MAIL_ALIAS))) {
            log.info("发件人名称别名未配置");
        }
        String smtpPort = properties.getProperty(MAIL_SMTP_PORT, "465");

        properties.put("mail.host", smtpHost);
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");

        // 使用SSL发送
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", smtpPort);
    }

    public void send(Mail mail) throws MessagingException, UnsupportedEncodingException, GeneralSecurityException {
        Session session = Session.getInstance(properties);
        session.setDebug("true".equals(properties.getProperty(MAIL_SMTP_DEBUG)));// 关闭调试
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(properties.getProperty(MAIL_ACCOUNT), properties.getProperty(MAIL_ALIAS)));
        message.setRecipient(Message.RecipientType.TO, mail.getTo());
        message.setSubject(mail.getSubject());
        message.setSentDate(new Date());

        Multipart multipart = new MimeMultipart();
        BodyPart text = new MimeBodyPart();
        text.setContent(mail.getContent(), "text/html;charset=utf-8");
        multipart.addBodyPart(text);

        // 若有附件，则添加附件
        for (String fileName : mail.getFiles()) {
            File file = new File(fileName);
            if (file.exists() && file.isFile()) {
                BodyPart accessory = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                accessory.setDataHandler(new DataHandler(source));
                accessory.setFileName(MimeUtility.encodeWord(file.getName()));
                multipart.addBodyPart(accessory);
            }
        }
        message.setContent(multipart);

        // 发送邮件
        Transport transport = session.getTransport();
        transport.connect(properties.getProperty(MAIL_ACCOUNT), properties.getProperty(MAIL_PASSWORD));
        transport.sendMessage(message, message.getAllRecipients());
    }

    public Mail createMail(String toEmailAddress) {
        Mail mail = new Mail(toEmailAddress);
        mail.setServer(this);
        return mail;
    }

    public Mail createMail(String toEmailAddress, String toPersonalName) {
        Mail mail = new Mail(toEmailAddress, toPersonalName);
        mail.setServer(this);
        return mail;
    }

    public static void main(String[] args) {
        sendAliyun();
        sendQQ();
    }

    private static void sendAliyun() {
        Properties prop = new Properties();
        prop.setProperty(MAIL_SMTP_HOST, "smtp.mxhichina.com");
        prop.setProperty(MAIL_ACCOUNT, "support@diteng.site");
//        prop.setProperty(MAIL_ALIAS, "地藤管家");
//        prop.setProperty(MAIL_PASSWORD, "");
//        prop.setProperty(MAIL_SMTP_DEBUG, "true");

        Mail mail = new SmtpServer(prop).createMail("sz9214e@qq.com", "JasonZhang");
        mail.addFile("d:\\a.txt");
        mail.addFile("d:\\b.txt");
        mail.send("test mail " + TDateTime.now().toString(), "1");
    }

    private static void sendQQ() {
        Properties prop = new Properties();
        prop.setProperty(MAIL_SMTP_HOST, "smtp.exmail.qq.com");
        prop.setProperty(MAIL_ACCOUNT, "develop@mimrc.com");
        prop.setProperty(MAIL_PASSWORD, "Mimrc2011");
        prop.setProperty(MAIL_ALIAS, "地藤管家");
        prop.setProperty(MAIL_SMTP_DEBUG, "true");

        Mail aliyunMail = new SmtpServer(prop).createMail("l1091462907@qq.com", "itjun");
        aliyunMail.send("test mail " + TDateTime.now().toString(), "1");
    }
}