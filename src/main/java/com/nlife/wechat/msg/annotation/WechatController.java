package com.nlife.wechat.msg.annotation;


import java.lang.annotation.*;

/**
 * 微信handle注解
 * @author nlife
 * @email xiamisspan@163.com
 * @date 2020/5/15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WechatController {
    String value() default "";
}
