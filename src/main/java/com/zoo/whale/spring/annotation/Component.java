package com.zoo.whale.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Date: 2026年 01月 27日
 * @Time:21时:45分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface  Component {

    String value() default "";

}
