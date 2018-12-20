package com.incarcloud.base.handler.dynamicrequest.request;

import com.incarcloud.base.request.RequestData;

public interface DynamicRequestHandler {
    Object handle(RequestData requestData) throws Throwable;
}
