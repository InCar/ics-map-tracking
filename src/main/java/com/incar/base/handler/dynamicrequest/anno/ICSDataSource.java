package com.incar.base.handler.dynamicrequest.anno;

import com.incar.base.config.DataSource;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ICSDataSource {
    DataSource value();
}
