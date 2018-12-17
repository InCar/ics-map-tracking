package com.incar.base.handler;

import com.incar.base.request.RequestData;
import com.incar.base.util.FileUtil;
import com.incar.base.config.Config;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 静态资源处理器
 *
 */
public class StaticResourceHandler extends PathStartWithHandler{
    /**
     * 定义文件后缀和响应类型映射
     */
    private final static Map<String,String> REQUEST_SUFFIX_TO_RESPONSE_TYPE=new HashMap<String,String>(){{
        put("jpeg","image/jpeg");
        put("jpg","image/jpg");
        put("png","image/png");
        put("html","text/html");
        put("js","text/javascript");
        put("css","text/css");
    }};

    public StaticResourceHandler(Config config) {
        super(config,config.getRequestStaticMappingPre());
    }

    @Override
    public void handle(RequestData requestData) {
        HttpServletResponse response=requestData.getResponse();
        Config config= requestData.getConfig();
        //1、获取子路径
        String subPath=requestData.getSubPath();
        //1.1、根据子路径和配置的静态文件请求路径、静态文件存放路径来拼装正确的静态文件相对地址
        String subFilePath=subPath.substring(config.getRequestStaticMappingPre().length()+1);
        String filePath=config.getFileStaticMappingPre()+subFilePath;
        //2、读取静态文件内容
        try(InputStream is=ClassLoader.getSystemResourceAsStream(filePath)){
            if(is==null){
                String msg="StaticResourceHandler dispatch path["+subPath+"] not exists";
                config.getLogger().log(Level.SEVERE,msg);
                throw new RuntimeException(msg);
            }
            response.setCharacterEncoding(config.getEncoding());
            response.setContentLength(is.available());
            setResponseType(subPath,response);
            FileUtil.write(is,response.getOutputStream());
        } catch (IOException e) {
            String msg="StaticResourceHandler dispatch path["+subPath+"] not exists";
            config.getLogger().log(Level.SEVERE,msg,e);
            throw new RuntimeException(msg);
        }
    }

    /**
     * 根据访问文件后缀设置response的响应类型
     * @param subPath
     * @param response
     */
    private void setResponseType(String subPath,HttpServletResponse response){
        int index= subPath.lastIndexOf(".");
        if(index!=-1){
            if(index<subPath.length()-1){
                String suffix= subPath.substring(index+1).toLowerCase();
                String responseType= REQUEST_SUFFIX_TO_RESPONSE_TYPE.get(suffix);
                if(responseType!=null){
                    response.setContentType(responseType);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
