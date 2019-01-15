package com.incarcloud.maptracking.service.impl.hbase;

import com.incarcloud.maptracking.service.VehicleService;
import com.incarcloud.maptracking.source.VehicleSource;
import com.incarcloud.skeleton.anno.ICSAutowire;
import com.incarcloud.skeleton.anno.ICSComponent;
import com.incarcloud.skeleton.anno.ICSConditionalOnMissingBean;
import com.incarcloud.skeleton.anno.ICSDataSource;
import com.incarcloud.skeleton.config.DataSource;
import com.incarcloud.skeleton.dao.jdbc.JdbcDataAccess;
import com.incarcloud.skeleton.dao.jdbc.RowHandler;
import com.incarcloud.skeleton.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;

import java.util.List;

@ICSDataSource(DataSource.HBase)
@ICSComponent
@ICSConditionalOnMissingBean(VehicleService.class)
public class VehicleServiceImpl extends BaseComponent implements VehicleService {
    @Override
    public List<VehicleSource> list(String vin) {
        return null;
    }

    @Override
    public PageResult<VehicleSource> page(String vin, Page page) {
        return null;
    }
}
