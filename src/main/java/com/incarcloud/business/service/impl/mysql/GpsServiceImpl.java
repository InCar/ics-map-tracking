package com.incarcloud.business.service.impl.mysql;

import com.incarcloud.base.config.DataSource;
import com.incarcloud.base.config.MysqlConfig;
import com.incarcloud.base.db.mysql.DBUtil;
import com.incarcloud.base.db.mysql.RowHandler;
import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSDataSource;
import com.incarcloud.base.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.service.GpsService;
import com.incarcloud.business.source.GpsSource;

import java.util.Date;
import java.util.List;

@ICSDataSource(DataSource.MYSQL)
@ICSComponent
public class GpsServiceImpl extends BaseComponent implements GpsService {
    public RowHandler<GpsSource> getGpsSourceRowHandler(){
        return rs->{
            String vin=rs.getString("vin");
            Float direction=rs.getFloat("direction");
            Double lng=rs.getDouble("lng");
            Double lat=rs.getDouble("lat");
            Date time=rs.getDate("time");
            return new GpsSource(vin,lng,lat,direction,time);
        };
    }

    @Override
    public List<GpsSource> listByVin(String vin) {
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        if(vin==null){
            String sql="select vin,lng,lat,direction,time from t_gps";
            return DBUtil.list(mysqlConfig,sql,rowHandler);
        }else{
            String sql="select vin,lng,lat,direction,time from t_gps where vin=?";
            return DBUtil.list(mysqlConfig,sql,rowHandler,vin);
        }
    }

    @Override
    public PageResult<GpsSource> pageByVin(String vin, Page page) {
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        if(vin==null){
            String countSql="select count(*) as num from t_gps";
            String sql="select vin,lng,lat,direction,time from t_gps limit ?,?";
            return DBUtil.page(mysqlConfig,countSql,sql,rowHandler,page);
        }else{
            String countSql="select count(*) as num from t_gps where vin=?";
            String sql="select vin,lng,lat,direction,time from t_gps where vin=? limit ?,?";
            return DBUtil.page(mysqlConfig,countSql,sql,rowHandler,page,vin);
        }
    }
}
