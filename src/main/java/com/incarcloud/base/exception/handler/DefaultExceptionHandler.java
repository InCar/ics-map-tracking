package com.incarcloud.base.exception.handler;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSConditionalOnMissingBean;
import com.incarcloud.base.json.JsonReader;
import com.incarcloud.base.message.JsonMessage;
import com.incarcloud.base.request.RequestData;
import com.incarcloud.base.util.ExceptionUtil;

import java.io.IOException;

@ICSComponent
@ICSConditionalOnMissingBean(ExceptionHandler.class)
public class DefaultExceptionHandler implements ExceptionHandler{

    @ICSAutowire
    JsonReader jsonReader;

    @Override
    public void resolveException(RequestData requestData, Throwable throwable) {
        requestData.getResponse().setCharacterEncoding(requestData.getConfig().getEncoding());
        ExceptionUtil.parseRealException(throwable);
        try {
            if(!requestData.getResponse().isCommitted()){
                JsonMessage result= ExceptionUtil.toJsonMessage(throwable);
                String msg=jsonReader.toJson(result);
                requestData.getResponse().setContentType("application/json");
                requestData.getResponse().getWriter().write(msg);
            }
        } catch (IOException e) {
            requestData.getResponse().setStatus(500);
            e.printStackTrace();
        }
    }
}
