package cn.zm1001.util.common.response;

import cn.zm1001.util.common.constant.HttpStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Dongd_Zhou
 * @desc 通用接口返回
 */
@Data
@Accessors(chain = true)
public class ApiBasicResponse implements Serializable {
    private static final long serialVersionUID = 5665939148793388029L;
    private boolean success;
    private int code;
    private String msg;

    public ApiBasicResponse() {

    }

    public ApiBasicResponse (boolean success, int code) {
        this.success = success;
        this.code = code;
    }

    public ApiBasicResponse (boolean success, int code, String msg) {
        this(success, code);
        this.msg = msg;
    }

    public static ApiBasicResponse success() {
        return new ApiBasicResponse(true, HttpStatus.SUCCESS);
    }

    public static ApiBasicResponse success(String msg) {
        return new ApiBasicResponse(true, HttpStatus.SUCCESS, msg);
    }

    public static ApiBasicResponse error(int code) {
        return new ApiBasicResponse(false, code);
    }

    public static ApiBasicResponse error(String msg) {
        return new ApiBasicResponse(false, HttpStatus.ERROR, msg);
    }

    public static ApiBasicResponse error(int code, String msg) {
        return new ApiBasicResponse(false, code, msg);
    }
}
