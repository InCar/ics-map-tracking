package com.incar.handler.impl.html;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.HashMap;
import java.util.Map;

/** 
* HTMLTemplateReader Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 11, 2018</pre> 
* @version 1.0 
*/ 
public class HTMLTemplateReaderTest { 

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
    *
    * Method: readTemplate(String fileName, Map<String,Object> dataMap)
    *
    */
    @Test
    public void testReadTemplate() throws Exception {

        {
            Map<String,Object> params = new HashMap<>();
            params.put("longitude", 111.1);
            params.put("latitude", 52.1);
            String s = HTMLTemplateReader.readTemplate("test.html", params);
            Assert.assertEquals("<html><head></head><body>坐标：经度：111.1  纬度：52.1</body></html>", s);
        }


        {
            Map<String, Object> params = new HashMap<>();
            params.put("longitude", 111.1);
            params.put("latitude", 52.1);
            try {
                HTMLTemplateReader.readTemplate("nofound.html", params);
            } catch (Exception e) {
                Assert.assertEquals(NullPointerException.class, e.getClass());
            }
        }
    }


} 
