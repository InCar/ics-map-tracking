package com.incar.base;

import com.incar.base.config.Config;
import com.incar.base.config.MysqlConfig;
import com.incar.base.context.Context;
import com.incar.base.context.DefaultContext;

public class Starter {
    public static Context getContext(){
        MysqlConfig mysqlConfig=new MysqlConfig(
                "jdbc:mysql://47.98.211.203:3306/test?characterEncoding=utf8&useSSL=false&rewriteBatchedStatements=true&serverTimezone=UTC",
                "root",
                "maptracking");

        Config config=new Config().withMysqlConfig(mysqlConfig);
        DefaultContext context=new DefaultContext(config).init();
        return context;
    }

    public static void main(String [] args){
        Context context=getContext();
        context.getConfig().getLogger().severe("test");
    }
}
