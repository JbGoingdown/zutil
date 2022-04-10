package cn.zm1001.util.web;

import cn.zm1001.util.common.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.AntPathMatcher;

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
}
