package com.incarcloud.base.context;

import com.incarcloud.base.request.RequestData;

public interface RequestHandler {
    void handleRequest(RequestData requestData);
}
