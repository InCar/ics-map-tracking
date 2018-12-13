package com.incar.base.handler;

import com.incar.base.config.Config;
import com.incar.base.request.RequestData;


/**
 * 匹配以 path 开头的请求
 */
public abstract class PathStartWithHandler extends PathHandler{
    public PathStartWithHandler(Config config,String path) {
        super(config,path);
    }

    @Override
    public Handler support(RequestData requestData) {
        String subPath=requestData.getSubPath();
        if(subPath.startsWith(path)){
            return this;
        }else{
            return null;
        }
    }
}
