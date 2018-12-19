package com.incar.base.context;

import com.incar.base.request.RequestData;

public interface Handler {
    void handle(RequestData requestData);
}
