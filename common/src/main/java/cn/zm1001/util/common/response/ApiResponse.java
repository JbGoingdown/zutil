package cn.zm1001.util.common.response;

import cn.zm1001.util.common.constant.HttpStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Dongd_Zhou
 * @desc 接口返回信息
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ApiResponse<T> extends ApiBasicResponse {
    private static final long serialVersionUID = 5447005148281491016L;
    private T data;

    public ApiResponse() {
        super();
    }

    public ApiResponse(boolean success, int code) {
        super(success, code);
    }

    public ApiResponse(boolean success, int code, String msg) {
        super(success, code, msg);
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>(true, HttpStatus.SUCCESS);
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ApiResponse<T> success(String msg, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>(true, HttpStatus.SUCCESS, msg);
        apiResponse.setData(data);
        return apiResponse;
    }
}
