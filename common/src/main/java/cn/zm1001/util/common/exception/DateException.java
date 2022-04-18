package cn.zm1001.util.common.exception;

/**
 * @Desc 日期转换异常
 * @Author Dongd_Zhou
 */
public class DateException extends RuntimeException {
    public DateException() {
        super();
    }

    public DateException(String message) {
        super(message);
    }

    public DateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateException(Throwable cause) {
        super(cause);
    }
}
