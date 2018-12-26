package com.incarcloud.base.handler.dynamicrequest.exception;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSConditionalOnMissingBean;
import com.incarcloud.base.handler.dynamicrequest.json.JsonReader;
import com.incarcloud.base.message.JsonMessage;
import com.incarcloud.base.request.RequestData;
import com.incarcloud.base.util.ExceptionUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
@ICSComponent
@ICSConditionalOnMissingBean(ExceptionHandler.class)
public class DefaultExceptionHandler implements ExceptionHandler{

    @ICSAutowire
    JsonReader jsonReader;

    @Override
    public void resolveException(RequestData requestData, Throwable throwable) {
        throwable.printStackTrace();
        try {
            if(!requestData.getResponse().isCommitted()){
                JsonMessage result= ExceptionUtil.toJsonMessage(throwable);
                String msg=jsonReader.toJson(result);
                requestData.getResponse().setCharacterEncoding(requestData.getConfig().getEncoding());
                requestData.getResponse().setContentType("application/json");
                requestData.getResponse().getWriter().write(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
