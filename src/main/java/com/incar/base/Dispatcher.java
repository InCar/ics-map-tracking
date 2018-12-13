package com.incar.base;

import com.incar.base.config.Config;
import com.incar.base.exception.NoHandlerException;
import com.incar.base.handler.*;
import com.incar.base.request.RequestData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 请求分发器
 * 请求的入口,通过调用
 * @see #dispatch(HttpServletRequest, HttpServletResponse)
 * 来接入请求
 *
 * 其中 handlerMap 存储的是该Dispatcher用到的Handler
 * 通过调用
 * @see Handler#support(RequestData) 来判断handler能否处理当前请求
 * 遍历的顺序按照
 * @see Ordered#getOrder() 正序定义
 * 当都不能处理请求时候抛出异常
 * @see NoHandlerException
 *
 * 分发器初始化逻辑参见
 * @see #initHandler()
 */
public class Dispatcher {
    private final static String DYNAMIC_REQUEST_HANDLER_NAME="dynamicRequest";
    private final static String STATIC_RESOURCE_HANDLER_NAME="staticResource";

    private Config config;

    private final Map<String,OrderedHandler> handlerMap =new HashMap<>();

    private List<OrderedHandler> handlerChain=new ArrayList<>();

    public Dispatcher(Config config) {
        this(config,null);
    }

    /**
     * 构造请求分发器
     * @param config
     * @param handlerMap
     */
    public Dispatcher(Config config, Map<String, OrderedHandler> handlerMap) {
        this.config=config;
        if(handlerMap!=null&&handlerMap.size()>0){
            this.handlerMap.putAll(handlerMap);
        }
        initHandler();
    }

    /**
     * 在每次添加或者移除handler后,重写生成
     * @see #handlerChain
     * 并排序
     */
    private void afterHandlerMapChanged(){
        List<OrderedHandler> tempList=new ArrayList<>(handlerMap.values());
        tempList.sort(OrderedComparator.INSTANCE);
        this.handlerChain=Collections.unmodifiableList(tempList);
    }

    /**
     * 初始化请求分发器
     * 会初始化
     * 1、静态资源处理器
     * @see StaticResourceHandler
     * 2、动态资源处理器
     * @see DynamicRequestHandler
     *
     */
    synchronized private void initHandler(){
        this.handlerMap.put(STATIC_RESOURCE_HANDLER_NAME,new StaticResourceHandler(config));
        this.handlerMap.put(DYNAMIC_REQUEST_HANDLER_NAME,new DynamicRequestHandler(config));
        afterHandlerMapChanged();
    }

    public Config getConfig() {
        return config;
    }

    /**
     * 新增或替换多个处理器
     * @param handlerMap
     * @return
     */
    synchronized public Dispatcher addOrReplaceHandler(Map<String,OrderedHandler> handlerMap){
        this.handlerMap.putAll(handlerMap);
        afterHandlerMapChanged();
        return this;
    }

    /**
     * 新增或替换一个处理器
     * @param name
     * @param handler
     * @return
     */
    synchronized public Handler addOrReplaceHandler(String name, OrderedHandler handler){
        OrderedHandler removedHandler=this.handlerMap.put(name,handler);
        afterHandlerMapChanged();
        return removedHandler;
    }

    /**
     * 移除多个处理器
     * @param names
     * @return
     */
    synchronized public List<OrderedHandler> remove(String ... names){
        List<OrderedHandler> removedHandlerList=new ArrayList<>();
        if(names==null||names.length==0){
            return removedHandlerList;
        }
        for (String name : names) {
            OrderedHandler removedHandler=this.handlerMap.remove(name);
            removedHandlerList.add(removedHandler);
        }
        afterHandlerMapChanged();
        return removedHandlerList;
    }

    /**
     * 更换动态请求处理器
     * @param handler
     * @return
     */
    synchronized public Dispatcher withDynamicRequestHandler(OrderedHandler handler){
        addOrReplaceHandler(DYNAMIC_REQUEST_HANDLER_NAME,handler);
        return this;
    }

    /**
     * 获取动态请求处理器
     * @return
     */
    public DynamicRequestHandler getDynamicRequestHandler(){
        return (DynamicRequestHandler)handlerMap.get(DYNAMIC_REQUEST_HANDLER_NAME);
    }

    /**
     * 更换静态资源处理器
     * @param handler
     * @return
     */
    synchronized public Dispatcher withStaticResourceHandler(OrderedHandler handler){
        addOrReplaceHandler(STATIC_RESOURCE_HANDLER_NAME,handler);
        return this;
    }

    /**
     * 获取静态资源处理器
     * @return
     */
    public StaticResourceHandler getStaticResourceHandler(){
        return (StaticResourceHandler)handlerMap.get(STATIC_RESOURCE_HANDLER_NAME);
    }




    /**
     * 依次经过每个处理器,没有处理器处理则抛出异常
     * @param request
     * @param response
     * @throws NoHandlerException
     */
    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws NoHandlerException{
        RequestData requestData=new RequestData(request,response,config);
        if(requestData.getSubPath()==null){
            throw new NoHandlerException(request.getRequestURI());
        }
        for (Handler handler : handlerChain) {
            Handler curHandler;
            if((curHandler=handler.support(requestData))!=null){
                curHandler.handle(requestData);
                return;
            }
        }
        throw new NoHandlerException(request.getRequestURI());
    }
}
