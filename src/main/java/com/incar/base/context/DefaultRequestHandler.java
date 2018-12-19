package com.incar.base.handler;

import com.incar.base.handler.dynamicrequest.anno.ICSAutowire;
import com.incar.base.handler.dynamicrequest.anno.ICSComponent;
import com.incar.base.handler.dynamicrequest.anno.ICSConditionalOnMissingBean;
import com.incar.base.handler.dynamicrequest.anno.ICSController;
import com.incar.base.handler.dynamicrequest.context.*;
import com.incar.base.handler.dynamicrequest.exception.DefaultExceptionHandler;
import com.incar.base.handler.dynamicrequest.json.JsonReader;
import com.incar.base.handler.dynamicrequest.request.DynamicRequest;
import com.incar.base.handler.dynamicrequest.request.impl.ICSSimpleRequest;
import com.incar.base.request.RequestData;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 动态请求处理器
 * 所有请求转发给handlerChain中的处理器,自己不作请求处理
 */
@ICSComponent
@ICSConditionalOnMissingBean(RequestHandler.class)
public class DefaultRequestHandler implements RequestHandler,Initialable{


    private Map<String,DynamicRequest> handlerMap=new ConcurrentHashMap<>();
    @ICSAutowire
    private JsonReader jsonReader;

    private Context context;

    public Map<String, DynamicRequest> getHandlerMap() {
        return handlerMap;
    }

    public JsonReader getJsonReader() {
        return jsonReader;
    }

    public DefaultRequestHandler withJsonReader(JsonReader jsonReader) {
        this.jsonReader = jsonReader;
        return this;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void handleRequest(RequestData requestData) {
        try {
            String subPath=requestData.getSubPath();
            DynamicRequest handler= handlerMap.get(subPath);
            if(handler==null){
                throw new RuntimeException("No Mapping Request["+subPath+"]");
            }
            Object res=handler.handle(requestData);
            HttpServletResponse response= requestData.getResponse();
            response.setCharacterEncoding(requestData.getConfig().getEncoding());
            response.getWriter().write(jsonReader.toJson(res));
        } catch (Throwable throwable) {
            DefaultExceptionHandler.INSTANCE.resolveException(requestData,throwable);
        }
    }

    @Override
    public void init(Context context) {
        this.context=context;
        if(context instanceof AutoScanner){
            Map<String,Object> beanMap= ((AutoScanner) context).getBeanMap();
            //获取所有ICSController注解的对象
            List<Object> objList=beanMap.values().stream().filter(e->e.getClass().getAnnotation(ICSController.class)!=null).collect(Collectors.toList());
            Map<String,ICSSimpleRequest> pathToMethodMap=new HashMap<>();
            for (Object controllerObj : objList) {
                List<ICSSimpleRequest> methodList= ICSSimpleRequest.generateByICSController(controllerObj);

                for (ICSSimpleRequest request : methodList) {
                    String key=request.getPath();
                    if(pathToMethodMap.containsKey(key)){
                        ICSSimpleRequest mapMethod= pathToMethodMap.get(key);
                        throw new RuntimeException("["+mapMethod.getControllerObj().getClass().getName()+"."+mapMethod.getMethod().getName()+"] requestMapping same as ["+request.getControllerObj().getClass().getName()+"."+request.getMethod().getName()+"]");
                    }else{
                        pathToMethodMap.put(key,request);
                    }
                }
            }
        }
    }
}
