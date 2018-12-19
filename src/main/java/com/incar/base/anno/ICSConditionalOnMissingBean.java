package com.incar.base.anno;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ICSConditionalOnMissingBean {
    Class[] value() default {};

    String name() default "";
}
