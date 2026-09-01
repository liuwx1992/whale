package com.zoo.whale.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZookeeperIdentity {

    /**
     * @return an String of the kind of elements an annotation type
     * can be applied to
     */
    String value() default "";


}
