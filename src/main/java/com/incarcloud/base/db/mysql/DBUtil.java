package com.incar.base.db.mysql;

import com.incar.base.config.MysqlConfig;
import com.incar.base.page.Page;
import com.incar.base.page.PageResult;
import com.incar.base.util.ClassUtil;
import com.incar.business.source.VehicleSource;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DBUtil {
    public static Connection getConn(MysqlConfig mysqlConfig) throws SQLException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e1) {
                throw new RuntimeException("Mysql Driver Class Not Exist");
            }
        }
        return DriverManager.getConnection(mysqlConfig.getUrl(),mysqlConfig.getUser(),mysqlConfig.getPassword());
    }

    public static <T>List<T> list(MysqlConfig mysqlConfig, String sql, RowHandler<T> rowHandler, Object ... params){
        try(Connection connection=getConn(mysqlConfig)){
            List<T> resultList=new ArrayList<>();
            PreparedStatement ps=connection.prepareStatement(sql);
            if(params!=null&&params.length>0){
                for(int i=1;i<=params.length;i++){
                    ps.setObject(i,params[i]);
                }
            }
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
               T t= rowHandler.apply(rs);
               resultList.add(t);
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T>PageResult<T> page(MysqlConfig mysqlConfig, String countSql, String sql, RowHandler<T> rowHandler, Page page, Object ... params){
        try(Connection connection=getConn(mysqlConfig)){
            PreparedStatement countPs=connection.prepareStatement(countSql);
            if(params!=null&&params.length>0){
                for(int i=1;i<=params.length;i++){
                    countPs.setObject(i,params[i]);
                }
            }
            ResultSet countRs= countPs.executeQuery();
            countRs.next();
            int count=countRs.getInt(1);
            if(count==0){
                return new PageResult<>(new ArrayList<>(),0);
            }else{
                PreparedStatement ps=connection.prepareStatement(sql);
                int len=0;
                if(params!=null&&(len=params.length)>0){
                    for(int i=1;i<=params.length;i++){
                        ps.setObject(i,params[i]);
                    }
                }
                ps.setObject(len+1,(page.getPageNum()-1)*page.getPageSize());
                ps.setObject(len+2,page.getPageSize());
                ResultSet rs= ps.executeQuery();
                List<T> dataList=new ArrayList<>();
                while(rs.next()){
                    T t= rowHandler.apply(rs);
                    dataList.add(t);
                }
                return new PageResult<>(dataList,count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
