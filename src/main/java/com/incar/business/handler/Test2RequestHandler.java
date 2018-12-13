package com.incar.business.handler;

import com.incar.base.config.Config;
import com.incar.base.handler.PathEqualHandler;
import com.incar.base.request.RequestData;

import java.io.IOException;

public class Test2RequestHandler extends PathEqualHandler {
    public Test2RequestHandler(Config config) {
        super(config,"/test");
    }

    @Override
    public void handle(RequestData requestData) {
        try {
            requestData.getResponse().getWriter().write("test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
