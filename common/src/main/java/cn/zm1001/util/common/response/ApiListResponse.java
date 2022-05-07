package cn.zm1001.util.common.response;

import cn.zm1001.util.common.constant.HttpStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Dongd_Zhou
 * @desc 返回列表信息
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ApiListResponse<T> extends ApiBasicResponse {
    private static final long serialVersionUID = -4318332291958790630L;
    private List<T> data;

    public ApiListResponse() {
        super();
    }

    public ApiListResponse(boolean success, int code) {
        super(success, code);
    }

    public ApiListResponse(boolean success, int code, String msg) {
        super(success, code, msg);
    }

    public static <T> ApiListResponse<T> success(List<T> data) {
        ApiListResponse<T> apiResponse = new ApiListResponse<>(true, HttpStatus.SUCCESS);
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ApiListResponse<T> success(String msg, List<T> data) {
        ApiListResponse<T> apiResponse = new ApiListResponse<>(true, HttpStatus.SUCCESS, msg);
        apiResponse.setData(data);
        return apiResponse;
    }
}
