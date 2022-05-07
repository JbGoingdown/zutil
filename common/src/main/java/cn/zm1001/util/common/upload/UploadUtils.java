package cn.zm1001.util.common.upload;

import cn.zm1001.util.common.JacksonUtils;
import cn.zm1001.util.common.StringUtils;
import cn.zm1001.util.common.http.HttpUtils;
import cn.zm1001.util.common.response.ApiBasicResponse;
import cn.zm1001.util.common.upload.UploadConst.HandlerImgType;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dongd_Zhou
 * @desc 图片上传
 */
@Slf4j
public class UploadUtils {

    /**
     * 上传通用图片
     *
     * @param url  上传地址
     * @param file 文件
     * @return 图片访问地址
     */
    public static String uploadCommonImg(String url, File file) {
        return uploadImg(url, file, HandlerImgType.COMMON, "/img", null);
    }

    /**
     * 上传通用图片
     *
     * @param url 上传地址
     * @param is  图片输入流
     * @return 图片访问地址
     */
    public static String uploadCommonImg(String url, InputStream is) {
        return uploadImg(url, is, HandlerImgType.COMMON, "/img", null);
    }

    /**
     * 上传头像
     *
     * @param url  上传地址
     * @param file 文件
     * @return 图片访问地址
     */
    public static String uploadUserHead(String url, File file) {
        return uploadImg(url, file, HandlerImgType.USER_HEAD, "/profile", null);
    }

    /**
     * 上传头像
     *
     * @param url 上传地址
     * @param is  图片输入流
     * @return 图片访问地址
     */
    public static String uploadUserHead(String url, InputStream is) {
        return uploadImg(url, is, HandlerImgType.USER_HEAD, "/profile", null);
    }

    /**
     * 上传图片
     * 大图(big)800*800，中图(middle)300*300，小图(small)100*100
     *
     * @param url  上传地址
     * @param file 文件
     * @return 图片访问地址(中图地址)
     */
    public static String uploadImg3(String url, File file) {
        return uploadImg(url, file, HandlerImgType.IMG3, "/img3", null);
    }

    /**
     * 上传图片
     * 大图(big)800*800，中图(middle)300*300，小图(small)100*100
     *
     * @param url 上传地址
     * @param is  图片输入流
     * @return 图片访问地址(中图地址)
     */
    public static String uploadImg3(String url, InputStream is) {
        return uploadImg(url, is, HandlerImgType.IMG3, "/img3", null);
    }

    /**
     * 上传图片
     *
     * @param url  上传地址
     * @param file 文件
     * @param type 图片处理类型
     * @param path 上传后存放目录(以/开头)
     * @return 图片访问地址
     */
    public static String uploadImg(String url, File file, HandlerImgType type, String path) {
        return uploadImg(url, file, type, path, null);
    }

    /**
     * 上传图片
     *
     * @param url  上传地址
     * @param is   图片输入流
     * @param type 图片处理类型
     * @param path 上传后存放目录(以/开头)
     * @return 图片访问地址
     */
    public static String uploadImg(String url, InputStream is, HandlerImgType type, String path) {
        return uploadImg(url, is, type, path, null);
    }

    /**
     * 上传图片
     *
     * @param url      上传地址
     * @param file     文件
     * @param type     图片处理类型
     * @param path     上传后存放目录(以/开头)
     * @param fileName 图片名称，入参后访问地址保留文件名
     * @return 图片访问地址
     */
    public static String uploadImg(String url, File file, HandlerImgType type, String path, String fileName) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("type", type.name());
        paramMap.put("path", path);
        if (StringUtils.isNotEmpty(fileName)) {
            paramMap.put("fileName", fileName);
        }
        String result = HttpUtils.upload(url, file, paramMap);
        final ApiBasicResponse rsp = JacksonUtils.parse(result, ApiBasicResponse.class);
        if (null == rsp || !rsp.isSuccess()) {
            return null;
        }
        return rsp.getMsg();
    }

    /**
     * 上传图片
     *
     * @param url      上传地址
     * @param is       图片输入流
     * @param type     图片处理类型
     * @param path     上传后存放目录(以/开头)
     * @param fileName 图片名称，入参后访问地址保留文件名
     * @return 图片访问地址
     */
    public static String uploadImg(String url, InputStream is, HandlerImgType type, String path, String fileName) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("type", type.name());
        paramMap.put("path", path);
        if (StringUtils.isNotEmpty(fileName)) {
            paramMap.put("fileName", fileName);
        }
        String result = HttpUtils.upload(url, is, paramMap);
        final ApiBasicResponse rsp = JacksonUtils.parse(result, ApiBasicResponse.class);
        if (null == rsp || !rsp.isSuccess()) {
            return null;
        }
        return rsp.getMsg();
    }
}
