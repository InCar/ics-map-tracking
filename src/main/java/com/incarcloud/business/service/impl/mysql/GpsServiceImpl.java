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
