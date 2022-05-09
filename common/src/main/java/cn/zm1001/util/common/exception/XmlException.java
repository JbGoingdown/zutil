package cn.zm1001.util.common.exception;

/**
 * @author Dongd_Zhou
 * @desc XML转换异常
 */
public class XmlException extends RuntimeException {
    public XmlException() {
        super();
    }

    public XmlException(String message) {
        super(message);
    }

    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlException(Throwable cause) {
        super(cause);
    }
}
