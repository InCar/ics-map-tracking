package com.incar.base.handler;

import com.incar.base.config.Config;
import com.incar.base.request.RequestData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 动态请求处理器
 * 所有请求转发给handlerChain中的处理器,自己不作请求处理
 */
public class DynamicRequestHandler implements OrderedHandler{
    private final List<OrderedHandler> handlerChain =new ArrayList<>();

    private Config config;
    public DynamicRequestHandler(Config config) {
        this.config=config;
    }

    /**
     * 在每次添加新的handler后,按照order排序
     */
    private void afterHandlerChainChanged(){
        this.handlerChain.sort(OrderedComparator.INSTANCE);
    }

    public DynamicRequestHandler(List<OrderedHandler> handlerChain) {
        this.handlerChain.addAll(handlerChain);
        afterHandlerChainChanged();

    }

    public List<OrderedHandler> getHandlerChain() {
        return handlerChain;
    }

    /**
     * 添加多个handler
     * @param handlers
     * @return
     */
    synchronized public DynamicRequestHandler addHandler(OrderedHandler ... handlers){
        if(handlers!=null&&handlers.length>0){
            handlerChain.addAll(Arrays.asList(handlers));
        }
        afterHandlerChainChanged();
        return this;
    }

    @Override
    public void handle(RequestData requestData) {
        //do nothing
    }

    @Override
    public Handler support(RequestData requestData) {
        for (Handler handler : handlerChain) {
            if(handler.support(requestData)!=null){
                return handler;
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
