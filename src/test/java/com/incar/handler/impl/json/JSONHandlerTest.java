package com.incar.handler.impl.json;

import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* JSONHandler Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 11, 2018</pre> 
* @version 1.0 
*/ 
public class JSONHandlerTest { 

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
    *
    * Method: request(Object msg)
    *
    */
    @Test
    public void testRequest() throws Exception {
        JSONHandler jh = new JSONHandler(new JSONReader() {
            @Override
            public String toJson(Object obj) {
                return JSON.toJSONString(obj);
            }
        });
        {
            String hello = jh.request("hello");
            Assert.assertEquals("\"hello\"", hello);
        }
        {
            String hello = jh.request("");
            Assert.assertEquals("\"\"", hello);
        }

        {
            Coo c = new Coo("jl",10);
            String hello = jh.request(c);
            Assert.assertEquals("{\"age\":10,\"name\":\"jl\"}", hello);
        }
    }

    /**
    *
    * Method: requestWow(Object param)
    *
    */
    @Test
    public void testRequestWow() throws Exception {
        JSONHandler jh = new JSONHandler(new JSONReader() {
            @Override
            public String toJson(Object obj) {
                return JSON.toJSONString(obj);
            }
        });
        {
            String hello = jh.requestWow("hello");
            Assert.assertEquals("参数值为：\"hello\"", hello);
        }
        {
            String hello = jh.requestWow("");
            Assert.assertEquals("参数值为：\"\"", hello);
        }

        {
            Coo c = new Coo("jl",10);
            String hello = jh.requestWow(c);
            Assert.assertEquals("参数值为：{\"age\":10,\"name\":\"jl\"}", hello);
        }
    }



    private static class Coo{
        private String name;
        private int age;

        public Coo(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

} 
