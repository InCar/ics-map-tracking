package com.incar.handler.impl.json;

import com.incar.handler.Handler;

import java.util.Map;

@SuppressWarnings("unchecked")
public class JSONHandler implements Handler<Object> {
    private JSONReader reader;

    public JSONHandler(JSONReader reader) {
        this.reader = reader;
    }

    @Override
    public String request(Object msg) {
        return reader.toJson(msg);
    }

    @Override
    public String requestWow(Object param) {
        if(null != param ) {
            return "参数值为：" + reader.toJson(param);
        } else {
            return null;
        }
    }
}
