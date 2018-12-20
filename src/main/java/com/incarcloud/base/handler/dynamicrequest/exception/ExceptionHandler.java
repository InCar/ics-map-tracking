package com.incar.base.handler.dynamicrequest.exception;

import com.incar.base.request.RequestData;

public interface ExceptionHandler {
    void resolveException(RequestData requestData,Throwable throwable);
}
