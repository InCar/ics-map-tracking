package com.incar.handler.impl.html;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** 
* HTMLHandler Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 11, 2018</pre> 
* @version 1.0 
*/ 
public class HTMLHandlerTest { 

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testRequest1() throws Exception {
        System.out.println(1);
    }


    /**
    *
    * Method: request(Object param)
    *
    */
    @Test
    public void testRequest() throws Exception {
        HTMLHandler htmlHandler = new HTMLHandler();
        Map<String,Object> params = new HashMap<>();
        params.put("longitude", 111.1);
        params.put("latitude", 52.1);
        String request = htmlHandler.request(params);
        Assert.assertEquals("<html><head></head><body>坐标：经度：111.1  纬度：52.1</body></html>", request);
    }

    /**
    *
    * Method: requestWow(Object param)
    *
    */
    @Test
    public void testRequestWow() throws Exception {
        HTMLHandler htmlHandler = new HTMLHandler();
        Map<String,Object> params = new HashMap<>();
        params.put("longitude", 111.1);
        params.put("latitude", 52.1);
        String request = htmlHandler.requestWow(params);
        Assert.assertEquals("<html><head></head><body>Hello, 坐标：经度：111.1  纬度：52.1.这是一个例子,用来测试Wow的,如果看到这段文字说明调用成功</body></html>", request);
    }


} 
