package com.nlife.wechat.msg.annotation;


import java.lang.annotation.*;

/**
 * 微信handle method 注解
 * @Author nlife
 * @Date 2020/5/15
 * @Email xiamisspan@163.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WechatMapping {

    /**
     * 匹配规则，正则表达式
     * @return
     */
    String[] value() default "";

    /**
     * 排序规则，多个方法都匹配成功时，此值越低的方法被执行
     * @return
     */
    int order() default 0;
}
