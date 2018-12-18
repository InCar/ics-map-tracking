package com.incar.base.handler.dynamicrequest.anno;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ICSAutowire {
    String value() default "";
}
