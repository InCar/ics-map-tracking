package com.incarcloud.base.handler.dynamicrequest.exception;

import com.incarcloud.base.request.RequestData;

public interface ExceptionHandler {
    void resolveException(RequestData requestData,Throwable throwable);
}
