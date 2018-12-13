package com.incar.base.handler;

import com.incar.base.request.RequestData;

public interface Handler {
    /**
     * 处理请求
     * @param requestData
     */
    void handle(RequestData requestData);

    /**
     * 返回支持的处理请求的handler
     * 如果没有支持的,返回null
     * @param requestData
     * @return
     */
    Handler support(RequestData requestData);
}
