package com.incar.base.request;

import com.incar.base.config.Config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 包含请求的数据
 */
public class RequestData {
    private HttpServletRequest request;
    private HttpServletResponse response;
    //根据config中的前缀解析出来的子路径
    private String subPath;
    private Config config;

    public RequestData(HttpServletRequest request, HttpServletResponse response,Config config) {
        this.request = request;
        this.response = response;
        this.config=config;
        this.subPath = getSubPath(request);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getSubPath() {
        return subPath;
    }

    public Config getConfig() {
        return config;
    }

    /**
     * 根据config解析子路径,如果返回null,则说明不支持
     * @param request
     * @return
     */
    private String getSubPath(HttpServletRequest request){
        String uri=request.getRequestURI();
        if(uri.startsWith(config.getMappingPre())){
            return uri.substring(config.getMappingPre().length());
        }else{
            return null;
        }
    }
}
