package cn.zm1001.util.web.xss.filter;

import cn.zm1001.util.common.StringUtils;
import cn.zm1001.util.web.UrlUtils;
import cn.zm1001.util.web.xss.wrapper.XssRequestWrapper;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Desc 防止XSS攻击的过滤器
 * @Author Dongd_Zhou
 */
public class XssFilter implements Filter {
    /** 排除链接 */
    public List<String> excludes = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        if (StringUtils.isNotEmpty(tempExcludes)) {
            String[] url = tempExcludes.split(",");
            Collections.addAll(excludes, url);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (handleExcludeURL(req)) {
            chain.doFilter(request, response);
            return;
        }
        XssRequestWrapper xssRequest = new XssRequestWrapper((HttpServletRequest) request);
        chain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request) {
        String url = request.getServletPath();
        String method = request.getMethod();
        // GET DELETE 不过滤
        if (null == method || method.matches(HttpMethod.GET.name()) || method.matches(HttpMethod.DELETE.name())) {
            return true;
        }
        return UrlUtils.matches(url, excludes);
    }

    @Override
    public void destroy() {

    }
}
