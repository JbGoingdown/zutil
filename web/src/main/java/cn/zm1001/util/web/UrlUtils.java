package cn.zm1001.util.web;

import cn.zm1001.util.common.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.AntPathMatcher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Desc URL处理工具类
 * @Author Dongd_Zhou
 */
public class UrlUtils {

    /**
     * 查找指定URL是否匹配指定任一地址
     *
     * @param url      需要匹配的url
     * @param patterns 匹配规则
     * @return 是否匹配
     */
    public static boolean matches(String url, List<String> patterns) {
        if (StringUtils.isEmpty(url) || CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (String pattern : patterns) {
            if (isMatch(url, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断URL是否与规则配置:
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     *
     * @param url     需要匹配的url
     * @param pattern 匹配规则
     * @return 是否匹配
     */
    public static boolean isMatch(String url, String pattern) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    /**
     * Url地址编码
     *
     * @param url URL地址
     * @return 编码后地址
     */
    public static String encode(String url) {
        return encode(url, StandardCharsets.UTF_8.name());
    }

    /**
     * Url地址编码
     *
     * @param url     URL地址
     * @param charset 字符集
     * @return 编码后地址
     */
    public static String encode(String url, String charset) {
        try {
            return URLEncoder.encode(url, charset);
        } catch (UnsupportedEncodingException ignored) {
        }
        return url;
    }

    /**
     * Url地址解码
     *
     * @param url URL地址
     * @return 解码后地址
     */
    public static String decode(String url) {
        return decode(url, StandardCharsets.UTF_8.name());
    }

    /**
     * Url地址解码
     *
     * @param url     URL地址
     * @param charset 字符集
     * @return 解码后地址
     */
    public static String decode(String url, String charset) {
        try {
            return URLDecoder.decode(url, charset);
        } catch (UnsupportedEncodingException ignored) {
        }
        return url;
    }
}
