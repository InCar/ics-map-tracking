package com.incarcloud.base;

import com.incarcloud.base.config.Config;
import com.incarcloud.base.config.JdbcConfig;
import com.incarcloud.base.context.Context;
import com.incarcloud.base.context.DefaultContext;
import com.incarcloud.base.dao.jdbc.JdbcDataAccess;

public class Starter {
    public static Context getContext(){
        JdbcConfig mysqlConfig=new JdbcConfig(
                JdbcDataAccess.MYSQL_DRIVER_CLASS_NAME_8,
                "jdbc:mysql://47.98.211.203:3306/test?characterEncoding=utf8&useSSL=false&rewriteBatchedStatements=true&serverTimezone=UTC",
                "root",
                "maptracking");
        Config config=new Config().withJdbcConfig(mysqlConfig);
        DefaultContext context=new DefaultContext(config).init();
        return context;
    }

    public static void main(String [] args){
        Context context=getContext();
        context.getConfig().getLogger().severe("test");
    }
}
