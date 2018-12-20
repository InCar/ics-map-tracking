package com.incarcloud.base.handler.dynamicrequest.convert.impl;

import com.incarcloud.base.handler.dynamicrequest.convert.ICSHttpParamConverter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

public class ArrayParamConverter implements ICSHttpParamConverter{
    public final static ArrayParamConverter INSTANCE=new ArrayParamConverter();
    @Override
    public Object convert(String[] source, Class targetType) {
        if(source==null){
            return null;
        }
        Class arrayType= targetType.getComponentType();
        if(source.length==0){
            return Array.newInstance(arrayType,0);
        }
        ICSHttpParamConverter subConverter;
        if(String.class.isAssignableFrom(arrayType)){
            subConverter=StringParamConverter.INSTANCE;
        }else if(Number.class.isAssignableFrom(arrayType)){
            subConverter=NumberParamConverter.INSTANCE;
        }else if(Date.class.isAssignableFrom(arrayType)){
            subConverter=DateParamConverter.INSTANCE;
        }else{
            String arrStr=Arrays.stream(source).reduce((e1,e2)->e1+","+e2).get();
            throw new RuntimeException("ArrayParamConverter Type["+targetType.getName()+"] Value["+arrStr+"] Not Support");
        }
        Object arr= Array.newInstance(arrayType,source.length);
        for(int i=0;i<=source.length-1;i++){
            Object val=subConverter.convert(new String[]{source[i]},arrayType);
            Array.set(arr,i,val);
        }
        return arr;
    }
}
