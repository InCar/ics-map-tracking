package com.incar.base.handler.dynamicrequest.anno;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ICSHttpRequestParam {
    String value();
    boolean required() default true;
}
