package com.incar.handler.impl.json;

import com.incar.handler.Handler;

import java.util.Map;

@SuppressWarnings("unchecked")
public class JSONHandler implements Handler<Object>{
    private JSONReader reader;
    public JSONHandler(JSONReader reader) {
        this.reader=reader;
    }

    @Override
    public String request(Object msg) {
        return reader.toJson(msg);
    }

    @Override
    public String requestWow(Object param) {
        if(param instanceof String){
            StringBuilder sb=new StringBuilder();
            sb.append("Hello,");
            sb.append(param);
            sb.append("这是一个例子,用来测试Wow的,如果看到这段文字说明调用成功");
            return sb.toString();
        }else if(param instanceof Map){
            ((Map) param).put("text","这是一个例子,用来测试Wow的,如果看到这段文字说明调用成功");
            return reader.toJson(param);
        }else{
            throw new RuntimeException("Param Type Not Support!");
        }
    }
}
