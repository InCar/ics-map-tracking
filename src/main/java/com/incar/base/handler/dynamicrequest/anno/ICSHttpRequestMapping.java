package com.incar.base.handler.dynamicrequest.anno;




import com.incar.base.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ICSHttpRequestMapping {
    String value();
    ICSHttpRequestMethodEnum method() default ICSHttpRequestMethodEnum.GET;
}
