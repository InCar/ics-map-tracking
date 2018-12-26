package com.incarcloud.base.context;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSConditionalOnMissingBean;
import com.incarcloud.base.anno.ICSController;
import com.incarcloud.base.exception.BaseRuntimeException;
import com.incarcloud.base.exception.handler.ExceptionHandler;
import com.incarcloud.base.json.JsonReader;
import com.incarcloud.base.handler.dynamicrequest.request.DynamicRequestHandler;
import com.incarcloud.base.handler.dynamicrequest.request.impl.SimpleRequestHandler;
import com.incarcloud.base.request.RequestData;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 动态请求处理器
 */
@ICSComponent("requestHandler")
@ICSConditionalOnMissingBean(name="requestHandler")
public class DefaultRequestHandler implements RequestHandler,Initializable {


    private Map<String,DynamicRequestHandler> handlerMap=new ConcurrentHashMap<>();
    @ICSAutowire
    private JsonReader jsonReader;

    @ICSAutowire
    private ExceptionHandler exceptionHandler;

    private Context context;

    public Map<String, DynamicRequestHandler> getHandlerMap() {
        return handlerMap;
    }

    public JsonReader getJsonReader() {
        return jsonReader;
    }

    public DefaultRequestHandler withJsonReader(JsonReader jsonReader) {
        this.jsonReader = jsonReader;
        return this;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public DefaultRequestHandler withExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void handleRequest(RequestData requestData) {
        try {
            String subPath=requestData.getSubPath();
            DynamicRequestHandler handler= handlerMap.get(subPath);
            if(handler==null){
                throw BaseRuntimeException.getException("No Mapping Request["+subPath+"]");
            }
            Object res=handler.handle(requestData);
            HttpServletResponse response= requestData.getResponse();
            response.setCharacterEncoding(requestData.getConfig().getEncoding());
            response.getWriter().write(jsonReader.toJson(res));
        } catch (Throwable throwable) {
            exceptionHandler.resolveException(requestData,throwable);
        }
    }

    @Override
    public void init(Context context) {
        this.context=context;
        if(context instanceof AutoScanner){
            Map<String,Object> beanMap= ((AutoScanner) context).getBeanMap();
            //获取所有ICSController注解的对象
            List<Object> objList=beanMap.values().stream().filter(e->e.getClass().getAnnotation(ICSController.class)!=null).collect(Collectors.toList());
            Map<String,SimpleRequestHandler> pathToMethodMap=new HashMap<>();
            for (Object controllerObj : objList) {
                List<SimpleRequestHandler> methodList= SimpleRequestHandler.generateByICSController(controllerObj);

                for (SimpleRequestHandler request : methodList) {
                    String key=request.getPath();
                    if(pathToMethodMap.containsKey(key)){
                        SimpleRequestHandler mapMethod= pathToMethodMap.get(key);
                        throw BaseRuntimeException.getException("["+mapMethod.getControllerObj().getClass().getName()+"."+mapMethod.getMethod().getName()+"] requestMapping same as ["+request.getControllerObj().getClass().getName()+"."+request.getMethod().getName()+"]");
                    }else{
                        pathToMethodMap.put(key,request);
                    }
                }
            }

            handlerMap.putAll(pathToMethodMap);
        }
    }
}
