package com.incar.base.db.mysql;

import com.incar.base.config.Config;
import com.incar.base.config.MysqlConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    public static Connection getConn(Config config) throws SQLException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e1) {
                throw new RuntimeException("Mysql Driver Class Not Exist");
            }
        }
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        return DriverManager.getConnection(mysqlConfig.getUrl(),mysqlConfig.getUser(),mysqlConfig.getPassword());
    }
}
