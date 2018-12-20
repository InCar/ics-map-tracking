package com.incarcloud.base.anno;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ICSComponent {
    String value() default "";

}
