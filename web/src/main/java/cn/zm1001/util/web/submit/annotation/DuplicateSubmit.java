package cn.zm1001.util.web.submit.annotation;

import java.lang.annotation.*;

/**
 * @Desc 重复提交
 * @Author Dongd_Zhou
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DuplicateSubmit {
    /** 间隔时间(ms)，多次在此间隔时间内提交为重复提交 */
    int interval() default 5000;

    /** 提示消息 */
    String message() default "不允许重复提交，请稍候再试";
}
