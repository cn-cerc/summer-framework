package cn.cerc.mis.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
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

import com.sun.mail.util.MailSSLSocketFactory;

import cn.cerc.core.ClassConfig;

public class MailSender {
    private static final ClassConfig config = new ClassConfig(MailSender.class, null);
    // 发送邮件的服务器地址和端口号
    private final String mailServerHost;
    private final String mailServerPort;
    // 邮件发送者的邮箱地址
    private final String fromAddress;
    // 邮件接收者的邮箱地址
    private String toAddress;
    // 登录发送邮件服务器的用户名和授权码(密码)
    private final String userName;
    private final String authorizeCode;
    // 邮件的主题
    private String subject;
    // 邮件的文本内容
    private String content;
    // 邮件的附件
    private File file;

    public MailSender() {
        mailServerHost = config.getString("mail.server.host", null);
        if (mailServerHost == null) {
            throw new RuntimeException("邮件发送服务器未配置");
        }
        mailServerPort = config.getString("mail.server.port", null);
        if (mailServerPort == null) {
            throw new RuntimeException("邮件发送服务器端口未配置");
        }
        fromAddress = config.getString("mail.server.fromAddress", null);
        if (fromAddress == null) {
            throw new RuntimeException("发件人邮箱地址未配置");
        }
        userName = fromAddress;
        authorizeCode = config.getString("mail.server.authorizeCode", null);
        if (authorizeCode == null) {
            throw new RuntimeException("发件人邮箱授权码未配置");
        }
    }

    // 设置邮件会话属性
    public Properties getProperties() throws GeneralSecurityException {
        Properties p = new Properties();
        p.put("mail.smtp.host", getMailServerHost());
        p.put("mail.smtp.port", getMailServerPort());
        // 设置SSL加密传输
        p.put("mail.smtp.socketFactory.port", getMailServerPort());
        MailSSLSocketFactory sf = new MailSSLSocketFactory("TLSv1.2");
        sf.setTrustAllHosts(true);
        p.put("mail.smtp.ssl.enable", "true");
        p.put("mail.smtp.ssl.socketFactory", sf);
        p.put("mail.smtp.socketFactory.fallback", "false");
        // 默认需要身份验证
        p.put("mail.smtp.auth", "true");
        return p;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getUserName() {
        return userName;
    }

    public String getAuthorizeCode() {
        return authorizeCode;
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

    public File getFiles() {
        return file;
    }

    public void setFiles(File file) {
        this.file = file;
    }

    public void send() throws MessagingException, UnsupportedEncodingException, GeneralSecurityException {
        // 身份验证，创建一个密码验证器
        MailAuthenticator authenticator = new MailAuthenticator(this.getUserName(), this.getAuthorizeCode());
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Properties p = this.getProperties();
        Session sendMailSession = Session.getDefaultInstance(p, authenticator);
        // 根据session创建一个邮件消息
        Message mailMessage = new MimeMessage(sendMailSession);
        // 创建邮件发送者的地址
        Address fromAddress = new InternetAddress(this.getFromAddress());
        // 设置邮件消息的发送者
        mailMessage.setFrom(fromAddress);
        // 创建邮件接收者的地址
        Address toAddress = new InternetAddress(this.getToAddress());
        // 设置邮件消息的接收者
        mailMessage.setRecipient(Message.RecipientType.TO, toAddress);
        // 设置邮件消息的主题
        mailMessage.setSubject(this.getSubject());
        // 设置邮件消息的发送时间
        mailMessage.setSentDate(new Date());
        // MimeMultipart类是一个容器类，包含MimeBodyPart类型的对象
        Multipart mainPart = new MimeMultipart();
        // 创建一个MimeBodyPart来包含普通文本
        BodyPart text = new MimeBodyPart();
        // 设置text的内容
        text.setContent(this.getContent(), "text/html;charset=utf-8");
        mainPart.addBodyPart(text);
        // 若有附件，则添加附件
        File file = this.getFiles();
        if (file != null && file.exists() && file.isFile()) {
            // 创建一个MimeBodyPart来包含附件
            BodyPart accessory = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            accessory.setDataHandler(new DataHandler(source));
            accessory.setFileName(MimeUtility.encodeWord(file.getName()));
            mainPart.addBodyPart(accessory);
        }
        // 将MimeMultipart对象设置为邮件内容
        mailMessage.setContent(mainPart);
        // 发送邮件
        Transport.send(mailMessage);
    }
}