package com.incar.base.handler;

import com.incar.base.config.Config;
import com.incar.base.request.RequestData;

/**
 * 匹配 等于 path 请求
 */
public abstract class PathEqualHandler extends PathHandler{
    public PathEqualHandler(Config config, String path) {
        super(config,path);
    }

    @Override
    public Handler support(RequestData requestData) {
        String subPath=requestData.getSubPath();
        if(subPath.equals(path)){
            return this;
        }else{
            return null;
        }
    }
}
