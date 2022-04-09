package cn.zm1001.util.poi.exception;

/**
 * @Desc Excel处理异常
 * @Author Dongd_Zhou
 */
public class ExcelException extends RuntimeException {
    public ExcelException() {
        super();
    }

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }
}
