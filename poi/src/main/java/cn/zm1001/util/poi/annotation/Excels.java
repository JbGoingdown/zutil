package cn.zm1001.util.poi.annotation;

import java.lang.annotation.*;

/**
 * @Desc Excel注解集
 * @Author Dongd_Zhou
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Excels {
    Excel[] value();
}
