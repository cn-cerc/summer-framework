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

import cn.cerc.core.ClassConfig;

/**
 * 阿里云企业邮箱客户端
 */
public class AliyunMailClient {
    private static final ClassConfig config = new ClassConfig(AliyunMailClient.class, null);

    private final String username;
    private final String password;
    private final Properties properties;
    private final Session session;

    private String to;
    private String subject;
    private String content;
    private File file;

    public AliyunMailClient() {
        username = config.getProperty("aliyun.mail.username");
        if (username == null) {
            throw new RuntimeException("发件人邮箱地址未配置");
        }

        password = config.getProperty("aliyun.mail.password");
        if (password == null) {
            throw new RuntimeException("发件人邮箱密码未配置");
        }

        properties = new Properties();
        properties.put("mail.host", "smtp.mxhichina.com");
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");

        session = Session.getInstance(properties);
        session.setDebug(false);
    }

    public void send() throws MessagingException, UnsupportedEncodingException, GeneralSecurityException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(this.getTo()));
        message.setSubject(this.getSubject());
        message.setSentDate(new Date());

        Multipart multipart = new MimeMultipart();
        BodyPart text = new MimeBodyPart();
        text.setContent(this.getContent(), "text/html;charset=utf-8");
        multipart.addBodyPart(text);

        // 若有附件，则添加附件
        File file = this.getFiles();
        if (file != null && file.exists() && file.isFile()) {
            BodyPart accessory = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            accessory.setDataHandler(new DataHandler(source));
            accessory.setFileName(MimeUtility.encodeWord(file.getName()));
            multipart.addBodyPart(accessory);
        }
        message.setContent(multipart);

        Transport transport = session.getTransport();
        transport.connect(username, password);
        transport.sendMessage(message, message.getAllRecipients());
    }

    public File getFiles() {
        return file;
    }

    public void setFiles(File file) {
        this.file = file;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}