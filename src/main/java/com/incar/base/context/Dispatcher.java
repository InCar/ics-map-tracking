package com.incar.base.context;

import com.incar.base.request.RequestData;

public interface Dispatcher {
    void dispatch(RequestData requestData);
}
