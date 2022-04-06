package cn.zm1001.util.mail.exception;

/**
 * @Desc 邮件异常
 * @Author Dongd_Zhou
 */
public class MailException extends RuntimeException {
    public MailException() {
        super();
    }

    public MailException(String message) {
        super(message);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailException(Throwable cause) {
        super(cause);
    }
}
