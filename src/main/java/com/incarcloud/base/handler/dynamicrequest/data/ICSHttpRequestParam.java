package com.incarcloud.base.handler.dynamicrequest.data;

public class ICSHttpRequestParam {
    private String name;
    private Class clazz;
    private Boolean required;

    public ICSHttpRequestParam(String name, Class clazz, Boolean required) {
        this.name = name;
        this.clazz = clazz;
        this.required = required;
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
}
