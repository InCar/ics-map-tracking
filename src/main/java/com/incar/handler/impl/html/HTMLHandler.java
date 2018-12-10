package com.incar.handler.impl.html;

import com.incar.handler.Handler;

import java.util.HashMap;
import java.util.Map;

public class HTMLHandler implements Handler<String>{
    @Override
    public String request(String param) {
        Map<String,String> dataMap=new HashMap<>();
        dataMap.put("text",param);
        return HTMLTemplateReader.readTemplate("test.html",dataMap);
    }

    @Override
    public String requestWow(String param) {
        Map<String,String> dataMap=new HashMap<>();
        dataMap.put("text",param);
        return HTMLTemplateReader.readTemplate("wow.html",dataMap);
    }
}
