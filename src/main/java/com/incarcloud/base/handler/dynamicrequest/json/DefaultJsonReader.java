package com.incar.base.handler.dynamicrequest.json;

import com.incar.base.anno.ICSComponent;
import com.incar.base.anno.ICSConditionalOnMissingBean;

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
