package com.incarcloud.base.context;

import com.incarcloud.base.request.RequestData;

public interface Dispatcher {
    void dispatch(RequestData requestData);
}
