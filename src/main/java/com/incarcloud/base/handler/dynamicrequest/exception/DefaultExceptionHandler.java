package com.incar.base.handler.dynamicrequest.exception;

import com.incar.base.request.RequestData;

import java.io.IOException;

public class DefaultExceptionHandler implements ExceptionHandler{
    public final static DefaultExceptionHandler INSTANCE=new DefaultExceptionHandler();
    @Override
    public void resolveException(RequestData requestData, Throwable throwable) {
        try {
            if(!requestData.getResponse().isCommitted()){
                requestData.getResponse().getWriter().write(throwable.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
