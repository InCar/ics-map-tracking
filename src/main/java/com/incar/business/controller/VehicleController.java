package com.incar.business.controller;

import com.incar.base.handler.dynamicrequest.anno.ICSHttpController;
import com.incar.base.handler.dynamicrequest.anno.ICSHttpRequestMapping;
import com.incar.base.handler.dynamicrequest.anno.ICSHttpRequestParam;

import java.util.Arrays;
import java.util.Date;

@ICSHttpController
@ICSHttpRequestMapping(value = "/test")
public class TestController {
    @ICSHttpRequestMapping("/test1")
    public String test1(
            @ICSHttpRequestParam(required = true,value = "name")
            String name,
            @ICSHttpRequestParam(required = false,value = "age")
            Integer age,
            @ICSHttpRequestParam(required = true,value = "time")
            Date time,
            @ICSHttpRequestParam(required = true,value = "ids")
            Long[] ids){
        return name+"  "+(age==null?"null":age)+ "  "+time.toInstant().toEpochMilli()+"  "+ Arrays.stream(ids).map(e->e.toString()).reduce((e1,e2)->e1+","+e2).get();
    }

}
