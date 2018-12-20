package com.incarcloud.base.anno;

import com.incarcloud.base.config.DataSource;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ICSDataSource {
    DataSource value();
}
