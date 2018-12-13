package com.incar.base.handler.dynamicrequest.json;

public class DefaultJsonReader implements JsonReader{
    @Override
    public String toJson(Object obj) {
        if(obj==null){
            return "";
        }
        return obj.toString();
    }
}
