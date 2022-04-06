package cn.zm1001.util.mail.pojo;

import lombok.AllArgsConstructor;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @Desc 邮件验证
 * @Author Dongd_Zhou
 */
@AllArgsConstructor
public class MailAuthenticator extends Authenticator {
    private final String userName;
    private final String password;

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
