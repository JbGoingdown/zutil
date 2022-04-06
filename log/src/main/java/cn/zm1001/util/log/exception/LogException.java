package cn.zm1001.util.log.exception;

/**
 * @Desc 日志异常
 * @Author Dongd_Zhou
 */
public class LogException extends RuntimeException {
    public LogException() {
        super();
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }
}
