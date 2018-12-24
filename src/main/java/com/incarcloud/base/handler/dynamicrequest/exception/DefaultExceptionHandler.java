package com.incarcloud.base.handler.dynamicrequest.exception;

import com.incarcloud.base.request.RequestData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class DefaultExceptionHandler implements ExceptionHandler{
    public final static DefaultExceptionHandler INSTANCE=new DefaultExceptionHandler();
    @Override
    public void resolveException(RequestData requestData, Throwable throwable) {
        throwable.printStackTrace();
        try {
            if(!requestData.getResponse().isCommitted()){
                Throwable realException= parseRealException(throwable);
                String msg=realException==null?"":realException.getMessage();
                requestData.getResponse().setCharacterEncoding(requestData.getConfig().getEncoding());
                requestData.getResponse().setContentType("text/plain");
                requestData.getResponse().getWriter().write(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 遇到如下两种情况继续深入取出异常信息:
     * 1、getCause()==null
     * 2、InvocationTargetException
     * @param throwable
     * @return
     */
    private Throwable parseRealException(Throwable throwable){
        //1、如果异常为空,返回null
        if(throwable==null){
            return null;
        }
        //2、获取其真实异常
        Throwable realException= throwable.getCause();
        //3、如果真实异常为当前异常
        if(realException==null){
            //4、如果真实异常为InvocationTargetException,则获取其目标异常
            if(throwable instanceof InvocationTargetException){
                return parseRealException(((InvocationTargetException)throwable).getTargetException());
            }else{
                //5、否则直接返回
                return throwable;
            }
        }else{
            //6、如果真实异常不为当前异常,则继续解析其真实异常
            return parseRealException(realException);
        }
    }
}
