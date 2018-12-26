package com.incarcloud.base.exception.handler;

import com.incarcloud.base.request.RequestData;

public interface ExceptionHandler {
    void resolveException(RequestData requestData,Throwable throwable);
}
