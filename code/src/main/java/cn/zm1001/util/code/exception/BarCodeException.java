package cn.zm1001.util.code.exception;

/**
 * @author Dongd_Zhou
 * @desc 条码异常
 */
public class BarCodeException extends RuntimeException {
    public BarCodeException() {
        super();
    }

    public BarCodeException(String message) {
        super(message);
    }

    public BarCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BarCodeException(Throwable cause) {
        super(cause);
    }
}
