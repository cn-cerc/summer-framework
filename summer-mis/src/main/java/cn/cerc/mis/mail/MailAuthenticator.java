package cn.cerc.mis.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
    private final String userName;
    private final String authorizeCode;

    public MailAuthenticator(String username, String password) {
        this.userName = username;
        this.authorizeCode = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, authorizeCode);
    }

}