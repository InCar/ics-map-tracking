package com.incarcloud.maptracking.service.impl.hbase;

import com.incarcloud.maptracking.data.GpsSplitSummary;
import com.incarcloud.maptracking.service.GpsService;
import com.incarcloud.maptracking.source.GpsSource;
import com.incarcloud.skeleton.anno.ICSComponent;
import com.incarcloud.skeleton.anno.ICSConditionalOnMissingBean;
import com.incarcloud.skeleton.anno.ICSDataSource;
import com.incarcloud.skeleton.config.DataSource;
import com.incarcloud.skeleton.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;

import java.util.Date;
import java.util.List;

@ICSDataSource(DataSource.HBase)
@ICSComponent
@ICSConditionalOnMissingBean(GpsService.class)
public class GpsServiceImpl  extends BaseComponent implements GpsService {
    @Override
    public List<GpsSource> list(String vin, Date startTime, Date endTime) {
        return null;
    }

    @Override
    public PageResult<GpsSource> page(String vin, Date startTime, Date endTime, Page page) {
        return null;
    }

    @Override
    public List<List<GpsSource>> listSplit(String vin, Integer num, Date startTime, Date endTime, Long gpsSplitTimeMills, Integer order) {
        return null;
    }

    @Override
    public List<GpsSplitSummary> listSplitSummary(String vin, Integer num, Date startTime, Date endTime, Long gpsSplitTimeMills, Integer order) {
        return null;
    }
}
