package com.incar.base.handler.dynamicrequest.convert.impl;

import com.incar.base.handler.dynamicrequest.convert.ICSHttpParamConverter;

public class StringParamConverter implements ICSHttpParamConverter<String>{
    public final static StringParamConverter INSTANCE=new StringParamConverter();
    @Override
    public String convert(String[] source, Class targetType) {
        if(source==null||source.length==0){
            return null;
        }else{
            return source[0];
        }
    }
}
