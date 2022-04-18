package cn.zm1001.util.common.response;

import cn.zm1001.util.common.constant.HttpStatus;

import java.util.HashMap;

/**
 * @Desc 请求响应结果
 * @Author Dongd_Zhou
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = -8136033216958761973L;

    /** 是否成功 */
    public static final String SUCCESS_TAG = "success";
    /** 状态码 */
    public static final String CODE_TAG = "code";
    /** 返回内容 */
    public static final String MSG_TAG = "msg";
    /** 数据对象 */
    public static final String DATA_TAG = "data";

    /** 初始化一个新创建的 R 对象，使其表示一个空消息。 */
    private R() {
        throw new UnsupportedOperationException();
    }

    /**
     * 初始化一个新创建的 R 对象
     *
     * @param success 是否成功
     * @param code    状态码
     * @param msg     返回内容
     */
    public R(boolean success, int code, String msg) {
        super.put(SUCCESS_TAG, success);
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 R 对象
     *
     * @param success 是否成功
     * @param code    状态码
     * @param msg     返回内容
     * @param data    数据对象
     */
    public R(boolean success, int code, String msg, Object data) {
        this(success, code, msg);
        if (null != data) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static R success() {
        return R.success("操作成功");
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static R success(String msg) {
        return R.success(msg, null);
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static R success(Object data) {
        return R.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static R success(String msg, Object data) {
        return new R(true, HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return 错误消息
     */
    public static R error() {
        return R.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 错误消息
     */
    public static R error(String msg) {
        return R.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 错误消息
     */
    public static R error(String msg, Object data) {
        return new R(false, HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 错误消息
     */
    public static R error(int code, String msg) {
        return new R(false, code, msg, null);
    }

    /**
     * 方便链式调用
     *
     * @param key   键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
