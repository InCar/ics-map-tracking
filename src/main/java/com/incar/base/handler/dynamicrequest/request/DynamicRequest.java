package com.incar.base.handler.dynamicrequest.request;

import com.incar.base.request.RequestData;

public interface DynamicRequest {
    Object handle(RequestData requestData) throws Throwable;
}
