package com.incarcloud.business.service.impl.mysql;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.config.DataSource;
import com.incarcloud.base.dao.jdbc.JdbcDataAccess;
import com.incarcloud.base.dao.jdbc.RowHandler;
import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSDataSource;
import com.incarcloud.base.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.service.VehicleService;
import com.incarcloud.business.source.VehicleSource;

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
