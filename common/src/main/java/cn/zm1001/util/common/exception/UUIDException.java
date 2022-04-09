package cn.zm1001.util.common.exception;

/**
 * @Desc UUID相关异常
 * @Author Dongd_Zhou
 */
public class UUIDException extends RuntimeException{
    public UUIDException() {
        super();
    }

    public UUIDException(String message) {
        super(message);
    }

    public UUIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public UUIDException(Throwable cause) {
        super(cause);
    }
}
