package com.incar.business.service.impl.mysql;

import com.incar.base.config.DataSource;
import com.incar.base.config.MysqlConfig;
import com.incar.base.db.mysql.DBUtil;
import com.incar.base.db.mysql.RowHandler;
import com.incar.base.handler.dynamicrequest.anno.ICSComponent;
import com.incar.base.handler.dynamicrequest.anno.ICSDataSource;
import com.incar.base.handler.dynamicrequest.component.BaseComponent;
import com.incar.base.page.Page;
import com.incar.base.page.PageResult;
import com.incar.business.service.GpsService;
import com.incar.business.source.GpsSource;
import com.incar.business.source.VehicleSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ICSDataSource(DataSource.MYSQL)
@ICSComponent
public class GpsServiceImpl extends BaseComponent implements GpsService{
    public RowHandler<GpsSource> getGpsSourceRowHandler(){
        return rs->{
            String gprscode=rs.getString("gprscode");
            double lng=rs.getDouble("lng");
            double lat=rs.getDouble("lat");
            return new GpsSource(gprscode,lng,lat);
        };
    }

    @Override
    public List<GpsSource> listByGprscode(String gprsCode) {
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        if(gprsCode==null){
            String sql="select gprscode,lng,lat from t_gps";
            return DBUtil.list(mysqlConfig,sql,rowHandler);
        }else{
            String sql="select gprscode,lng,lat from t_gps where gprscode=?";
            return DBUtil.list(mysqlConfig,sql,rowHandler,gprsCode);
        }
    }

    @Override
    public PageResult<GpsSource> pageByGprscode(String gprsCode, Page page) {
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        if(gprsCode==null){
            String countSql="select count(*) as num from t_gps";
            String sql="select gprscode,lng,lat from t_gps limit ?,?";
            return DBUtil.page(mysqlConfig,countSql,sql,rowHandler,page);
        }else{
            String countSql="select count(*) as num from t_gps where gprscode=?";
            String sql="select gprscode,lng,lat from t_gps where gprscode=? limit ?,?";
            return DBUtil.page(mysqlConfig,countSql,sql,rowHandler,page,gprsCode);
        }
    }
}
