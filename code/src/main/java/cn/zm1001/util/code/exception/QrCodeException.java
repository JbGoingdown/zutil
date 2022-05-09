package cn.zm1001.util.code.exception;

/**
 * @author Dongd_Zhou
 * @desc 二维码异常
 */
public class QrCodeException extends RuntimeException {
    public QrCodeException() {
        super();
    }

    public QrCodeException(String message) {
        super(message);
    }

    public QrCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public QrCodeException(Throwable cause) {
        super(cause);
    }
}
