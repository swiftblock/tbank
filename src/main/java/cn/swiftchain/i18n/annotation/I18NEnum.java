package cn.swiftchain.i18n.annotation;

import java.lang.annotation.*;

/**
 * 被此注解的属性可以用指定的key读取国际化信息
 *
 * @author baizhengwen
 * @date 2019-01-09
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18NEnum {
    /**
     * 枚举类
     *
     * @return
     */
    Class<?> value();
}
