package cn.zm1001.util.common.exception;

/**
 * @Desc SQL异常
 * @Author Dongd_Zhou
 */
public class SQLException extends RuntimeException{
    public SQLException() {
        super();
    }

    public SQLException(String message) {
        super(message);
    }

    public SQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLException(Throwable cause) {
        super(cause);
    }
}
