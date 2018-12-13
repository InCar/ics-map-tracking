package com.incar.business;

import com.incar.base.config.Config;
import com.incar.base.Dispatcher;


public class MapTrackingStarter {
    public static Dispatcher getDispatcher(){
        Config config=new Config();
        Dispatcher dispatcher=new Dispatcher(config);
        dispatcher.getStaticResourceHandler();
        return dispatcher;
    }

    public static void main(String [] args){
        Dispatcher dispatcher= MapTrackingStarter.getDispatcher();
        dispatcher.getConfig().getLogger().severe("test");
    }
}
