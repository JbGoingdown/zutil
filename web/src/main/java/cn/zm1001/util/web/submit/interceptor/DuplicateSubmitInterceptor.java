package cn.zm1001.util.web.submit.interceptor;

import cn.zm1001.util.common.response.AjaxResult;
import cn.zm1001.util.web.ServletUtils;
import cn.zm1001.util.web.submit.annotation.DuplicateSubmit;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Desc 重复提交拦截器
 * @Author Dongd_Zhou
 */
public abstract class DuplicateSubmitInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            DuplicateSubmit annotation = method.getAnnotation(DuplicateSubmit.class);
            if (null != annotation) {
                if (isRepeatSubmit(request, annotation)) {
                    AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                    ServletUtils.write(response, JSONObject.toJSONString(ajaxResult));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, DuplicateSubmit annotation);
}