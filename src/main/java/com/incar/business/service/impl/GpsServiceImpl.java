package com.incar.business.service.impl;

import com.incar.base.db.mysql.DBUtil;
import com.incar.base.handler.dynamicrequest.anno.ICSComponent;
import com.incar.base.handler.dynamicrequest.component.BaseComponent;
import com.incar.base.page.Page;
import com.incar.base.page.PageResult;
import com.incar.business.service.GpsService;
import com.incar.business.source.GpsSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ICSComponent
public class GpsServiceImpl extends BaseComponent implements GpsService{
    @Override
    public List<GpsSource> listByGprscode(String gprsCode) {
        try(Connection connection= DBUtil.getConn(config)){
            List<GpsSource> resultList=new ArrayList<>();
            ResultSet rs;
            if(gprsCode==null){
                String sql="select gprscode,lng,lat from t_gps";
                rs=connection.prepareStatement(sql).executeQuery();
            }else{
                String sql="select gprscode,lng,lat from t_gps where gprscode=?";
                PreparedStatement ps=connection.prepareStatement(sql);
                ps.setString(1,gprsCode);
                rs= ps.executeQuery();
            }
            while(rs.next()){
                String gprscode=rs.getString("gprscode");
                double lng=rs.getDouble("lng");
                double lat=rs.getDouble("lat");
                resultList.add(new GpsSource(gprscode,lng,lat));
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException("Sql Executor Error["+e.getMessage()+"]");
        }
    }

    @Override
    public PageResult<GpsSource> pageByGprscode(String gprsCode, Page page) {
        try(Connection connection= DBUtil.getConn(config)){
            if(gprsCode==null){
                String countSql="select count(*) as num from t_gps";
                ResultSet countRs=connection.prepareStatement(countSql).executeQuery();
                countRs.next();
                int num=countRs.getInt("num");
                if(num==0){
                    return new PageResult<>(new ArrayList<>(),0);
                }else{
                    List<GpsSource> dataList=new ArrayList<>();
                    String sql="select gprscode,lng,lat from t_gps limit ?,?";
                    PreparedStatement ps=connection.prepareStatement(sql);
                    ps.setInt(1,(page.getPageNum()-1)*page.getPageSize());
                    ps.setInt(2,page.getPageSize());
                    ResultSet rs= ps.executeQuery();
                    while(rs.next()){
                        String gprscode=rs.getString("gprscode");
                        double lng=rs.getDouble("lng");
                        double lat=rs.getDouble("lat");
                        dataList.add(new GpsSource(gprscode,lng,lat));
                    }
                    return new PageResult<>(dataList,num);
                }
            }else{
                String countSql="select count(*) as num from t_gps where gprscode=?";
                PreparedStatement countPs=connection.prepareStatement(countSql);
                countPs.setString(1,gprsCode);
                ResultSet countRs=countPs.executeQuery();
                countRs.next();
                int num=countRs.getInt("num");
                if(num==0){
                    return new PageResult<>(new ArrayList<>(),0);
                }else{
                    List<GpsSource> dataList=new ArrayList<>();
                    String sql="select gprscode,lng,lat from t_gps where gprscode=? limit ?,?";
                    PreparedStatement ps=connection.prepareStatement(sql);
                    ps.setString(1,gprsCode);
                    ps.setInt(2,(page.getPageNum()-1)*page.getPageSize());
                    ps.setInt(3,page.getPageSize());
                    ResultSet rs= ps.executeQuery();
                    while(rs.next()){
                        String gprscode=rs.getString("gprscode");
                        double lng=rs.getDouble("lng");
                        double lat=rs.getDouble("lat");
                        dataList.add(new GpsSource(gprscode,lng,lat));
                    }
                    return new PageResult<>(dataList,num);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Sql Executor Error["+e.getMessage()+"]");
        }
    }
}
