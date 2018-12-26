package com.incarcloud.base.json;

import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSConditionalOnMissingBean;

@ICSComponent
@ICSConditionalOnMissingBean(JsonReader.class)
public class DefaultJsonReader implements JsonReader{
    @Override
    public String toJson(Object obj) {
        if(obj==null){
            return "";
        }
        return obj.toString();
    }

}
