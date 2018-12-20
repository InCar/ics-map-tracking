package com.incarcloud.base.exception;

public class NoHandlerException extends Exception{
    private String uri;

    public String getUri() {
        return uri;
    }
    public NoHandlerException(String uri) {
        super("No Request["+uri +"] Handler");
        this.uri=uri;
    }
}
