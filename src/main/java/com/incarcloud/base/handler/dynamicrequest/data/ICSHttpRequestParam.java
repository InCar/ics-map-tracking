package com.incarcloud.base.handler.dynamicrequest.data;

public class ICSHttpRequestParam {
    private String name;
    private Class clazz;
    private Boolean required;
    private String defaultValue;

    public ICSHttpRequestParam(String name, Class clazz, Boolean required,String defaultValue) {
        this.name = name;
        this.clazz = clazz;
        this.required = required;
        this.defaultValue=defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
