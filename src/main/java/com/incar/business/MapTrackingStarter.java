package com.incar.business;

import com.incar.base.config.Config;
import com.incar.base.Dispatcher;
import com.incar.business.handler.Test2RequestHandler;
import com.incar.business.handler.TestRequestHandler;


public class MapTrackingStarter {
    public static Dispatcher getDispatcher(){
        Config config=new Config();
        Dispatcher dispatcher=new Dispatcher(config);
        dispatcher.getStaticResourceHandler();
        dispatcher.getDynamicRequestHandler().addHandler(new TestRequestHandler(config));
        dispatcher.getDynamicRequestHandler().addHandler(new Test2RequestHandler(config));
        return dispatcher;
    }

    public static void main(String [] args){
        Dispatcher dispatcher= MapTrackingStarter.getDispatcher();
        dispatcher.getConfig().getLogger().severe("test");
    }
}
