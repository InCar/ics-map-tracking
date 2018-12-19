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
import com.incar.business.service.VehicleService;
import com.incar.business.source.VehicleSource;

import java.util.List;
@ICSDataSource(DataSource.MYSQL)
@ICSComponent
public class VehicleServiceImpl extends BaseComponent implements VehicleService{
    private RowHandler<VehicleSource> getVehicleSourceRowHandler(){
        return rs->{
            String gprscode=rs.getString("gprscode");
            String plateNo=rs.getString("plate_no");
            return new VehicleSource(gprscode,plateNo);
        };
    }

    @Override
    public List<VehicleSource> listByGprscode(String gprsCode) {
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<VehicleSource> rowHandler=getVehicleSourceRowHandler();
        if(gprsCode==null){
            String sql="select gprscode,plate_no from t_vehicle";
            return DBUtil.list(mysqlConfig,sql,rowHandler);
        }else{
            String sql="select gprscode,plate_no from t_vehicle where gprscode=?";
            return DBUtil.list(mysqlConfig,sql,rowHandler,gprsCode);
        }
    }

    @Override
    public PageResult<VehicleSource> pageByGprscode(String gprsCode,Page page) {
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<VehicleSource> rowHandler=getVehicleSourceRowHandler();
        if(gprsCode==null){
            String countSql="select count(*) as num from t_vehicle";
            String sql="select gprscode,plate_no from t_vehicle limit ?,?";
            return DBUtil.page(mysqlConfig,countSql,sql,rowHandler,page);
        }else{
            String countSql="select count(*) as num from t_vehicle where gprscode=?";
            String sql="select gprscode,plate_no from t_vehicle where gprscode=? limit ?,?";
            return DBUtil.page(mysqlConfig,countSql,sql,rowHandler,page,gprsCode);
        }
    }
}
