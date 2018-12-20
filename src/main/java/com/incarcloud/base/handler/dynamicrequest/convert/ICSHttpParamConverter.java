package com.incar.base.handler.dynamicrequest.convert;

public interface ICSHttpParamConverter<T> {
    T convert(String[] source, Class targetType);
}
