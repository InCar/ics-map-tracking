package com.incar.handler.impl.html;

import com.incar.handler.Handler;

import java.util.HashMap;
import java.util.Map;

public class HTMLHandler implements Handler<Object>{
    @Override
    public String request(Object param) {
        return HTMLTemplateReader.readTemplate("yb.html",(Map)param);
    }

    @Override
    public String requestWow(Object param) {
        return HTMLTemplateReader.readTemplate("wow.html",(Map)param);
    }
}
