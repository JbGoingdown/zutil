package cn.zm1001.util.crypto.exception;

/**
 * @Desc 加解密异常
 * @Author Dongd_Zhou
 */
public class CryptoException extends RuntimeException {
    public CryptoException() {
        super();
    }

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(Throwable cause) {
        super(cause);
    }
}
