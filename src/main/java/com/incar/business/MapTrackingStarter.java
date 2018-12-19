package com.incar.business;

import com.incar.base.config.Config;
import com.incar.base.Dispatcher;
import com.incar.base.config.DataSource;
import com.incar.base.config.MysqlConfig;


public class MapTrackingStarter {
    public static Dispatcher getDispatcher(){
        MysqlConfig mysqlConfig=new MysqlConfig(
                "jdbc:mysql://47.98.211.203:3306/test?characterEncoding=utf8&useSSL=false&rewriteBatchedStatements=true&serverTimezone=UTC",
                "root",
                "maptracking");

        Config config=new Config().withMysqlConfig(mysqlConfig).withDataSource(DataSource.MYSQL);
        Dispatcher dispatcher=new Dispatcher(config);
        dispatcher.getStaticResourceHandler();
        return dispatcher;
    }

    public static void main(String [] args){
        Dispatcher dispatcher= MapTrackingStarter.getDispatcher();
        dispatcher.getConfig().getLogger().severe("test");
    }
}
