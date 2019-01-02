package com.incarcloud.maptracking.service.impl.mysql;

import com.incarcloud.skeleton.anno.ICSAutowire;
import com.incarcloud.skeleton.config.DataSource;
import com.incarcloud.skeleton.dao.jdbc.JdbcDataAccess;
import com.incarcloud.skeleton.dao.jdbc.RowHandler;
import com.incarcloud.skeleton.anno.ICSComponent;
import com.incarcloud.skeleton.anno.ICSDataSource;
import com.incarcloud.skeleton.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;
import com.incarcloud.maptracking.service.VehicleService;
import com.incarcloud.maptracking.source.VehicleSource;

import java.util.List;
@ICSDataSource(DataSource.JDBC)
@ICSComponent
public class VehicleServiceImpl extends BaseComponent implements VehicleService {
    @ICSAutowire
    JdbcDataAccess dataAccess;
    private RowHandler<VehicleSource> getVehicleSourceRowHandler(){
        return rs->{
            String vin=rs.getString("vin");
            String plateNo=rs.getString("plate_no");
            return new VehicleSource(vin,plateNo);
        };
    }

    @Override
    public List<VehicleSource> listByVin(String vin) {
        RowHandler<VehicleSource> rowHandler=getVehicleSourceRowHandler();
        if(vin==null){
            String sql="select vin,plate_no from t_vehicle";
            return dataAccess.list(sql,rowHandler);
        }else{
            String sql="select vin,plate_no from t_vehicle where vin=?";
            return dataAccess.list(sql,rowHandler,vin);
        }
    }

    @Override
    public PageResult<VehicleSource> pageByVin(String vin, Page page) {
        RowHandler<VehicleSource> rowHandler=getVehicleSourceRowHandler();
        if(vin==null){
            String countSql="select count(*) as num from t_vehicle";
            String sql="select vin,plate_no from t_vehicle limit ?,?";
            return dataAccess.page(countSql,sql,rowHandler,page);
        }else{
            String countSql="select count(*) as num from t_vehicle where vin=?";
            String sql="select vin,plate_no from t_vehicle where vin=? limit ?,?";
            return dataAccess.page(countSql,sql,rowHandler,page,vin);
        }
    }
}
