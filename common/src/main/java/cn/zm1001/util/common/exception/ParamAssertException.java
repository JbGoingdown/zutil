package cn.zm1001.util.common.exception;

/**
 * @Desc 断言异常
 * @Author Dongd_Zhou
 */
public class ParamAssertException extends RuntimeException{
    public ParamAssertException() {
        super();
    }

    public ParamAssertException(String message) {
        super(message);
    }

    public ParamAssertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamAssertException(Throwable cause) {
        super(cause);
    }
}
