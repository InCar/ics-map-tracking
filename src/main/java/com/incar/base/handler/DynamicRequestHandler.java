package com.incar.base.handler;

import com.incar.base.config.Config;
import com.incar.base.handler.dynamicrequest.anno.ICSController;
import com.incar.base.handler.dynamicrequest.context.ICSContext;
import com.incar.base.handler.dynamicrequest.exception.DefaultExceptionHandler;
import com.incar.base.handler.dynamicrequest.json.DefaultJsonReader;
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
public class DynamicRequestHandler implements OrderedHandler{

    private final static String[] DEFAULT_SCAN_PACKAGES=new String[]{"com.incar"};

    private Map<String,DynamicRequest> handlerMap=new ConcurrentHashMap<>();

    private String [] scanPackages;

    public DynamicRequestHandler(Config config,String ...scanPackages) {
        this.config=config;
        this.jsonReader=new DefaultJsonReader();
        if(scanPackages==null||scanPackages.length==0){
            this.scanPackages=DEFAULT_SCAN_PACKAGES;
        }else{
            this.scanPackages=scanPackages;
        }
        appendPackagesMappingIfNotExist();
    }

    private Config config;

    //json转换器
    private JsonReader jsonReader;

    public Map<String, DynamicRequest> getHandlerMap() {
        return handlerMap;
    }

    public String[] getScanPackages() {
        return scanPackages;
    }

    public Config getConfig() {
        return config;
    }

    public JsonReader getJsonReader() {
        return jsonReader;
    }

    public DynamicRequestHandler withJsonReader(JsonReader jsonReader) {
        this.jsonReader = jsonReader;
        return this;
    }

    @Override
    public void handle(RequestData requestData) {
        try {
            Object res=handlerMap.get(requestData.getSubPath()).handle(requestData);
            HttpServletResponse response= requestData.getResponse();
            response.setCharacterEncoding(config.getEncoding());
            response.getWriter().write(jsonReader.toJson(res));
        } catch (Throwable throwable) {
            DefaultExceptionHandler.INSTANCE.resolveException(requestData,throwable);
        }
    }

    @Override
    public Handler support(RequestData requestData) {
        if(handlerMap.containsKey(requestData.getSubPath())){
            return this;
        }else {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return 10000;
    }




    /**
     * 添加自定义注解
     * @see ICSController
     * 的映射类到其中
     */
    private void appendPackagesMappingIfNotExist(){
        //初始化ics环境
        ICSContext.init(config,scanPackages);
        //获取所有ICSController注解的对象
        List<Object> objList=ICSContext.getBeanMap().values().stream().filter(e->e.getClass().getAnnotation(ICSController.class)!=null).collect(Collectors.toList());
        for (Object controllerObj : objList) {
            List<ICSSimpleRequest> methodList= ICSSimpleRequest.generateByICSController(controllerObj);

            Map<String,ICSSimpleRequest> pathToMethodMap=new ConcurrentHashMap<>();
            for (ICSSimpleRequest request : methodList) {
                String key=request.getPath();
                if(pathToMethodMap.containsKey(key)){
                    ICSSimpleRequest mapMethod= pathToMethodMap.get(key);
                    throw new RuntimeException("["+mapMethod.getControllerObj().getClass().getName()+"."+mapMethod.getMethod().getName()+"] requestMapping same as ["+request.getControllerObj().getClass().getName()+"."+request.getMethod().getName()+"]");
                }else{
                    pathToMethodMap.put(key,request);
                }
            }

            //如果其中已经存在映射关系的处理器,则忽略默认的处理器,因为可能是用户重写了此处理器
            pathToMethodMap.forEach((k,v)->{
                handlerMap.putIfAbsent(k,v);
            });
        }
    }
}
