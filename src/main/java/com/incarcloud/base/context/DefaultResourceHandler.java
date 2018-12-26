package com.incarcloud.base.context;

import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSConditionalOnMissingBean;
import com.incarcloud.base.exception.BaseRuntimeException;
import com.incarcloud.base.request.RequestData;
import com.incarcloud.base.util.FileUtil;
import com.incarcloud.base.config.Config;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 静态资源处理器
 */
@ICSComponent("resourceHandler")
@ICSConditionalOnMissingBean(name="resourceHandler")
public class DefaultResourceHandler implements ResourceHandler,Initializable {

    private Context context;

    public Context getContext() {
        return context;
    }

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

    @Override
    public void handleResource(RequestData requestData) {
        HttpServletResponse response=requestData.getResponse();
        Config config= context.getConfig();
        //1、获取子路径
        String subPath=requestData.getSubPath();
        //1.1、根据子路径和配置的静态文件请求路径、静态文件存放路径来拼装正确的静态文件相对地址
        String subFilePath=subPath.substring(config.getRequestStaticMappingPre().length());
        String filePath=config.getFileStaticMappingPre()+subFilePath;
        filePath=filePath.substring(1);
        //2、读取静态文件内容
        try(InputStream is=ClassLoader.getSystemResourceAsStream(filePath)){
            if(is==null){
                String msg="ResourceHandler path["+subPath+"] not exists";
                config.getLogger().log(Level.SEVERE,msg);
                throw BaseRuntimeException.getException(msg);
            }
            response.setCharacterEncoding(config.getEncoding());
            response.setContentLength(is.available());
            setResponseType(subPath,response);
            FileUtil.write(is,response.getOutputStream());
        } catch (IOException e) {
            throw BaseRuntimeException.getException(e);
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
    public void init(Context context) {
        this.context=context;
    }
}
