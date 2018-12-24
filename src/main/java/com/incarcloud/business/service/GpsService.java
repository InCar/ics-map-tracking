package com.incarcloud.business.service;

import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.source.GpsSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface GpsService {
    default List<GpsSource> listByVin(String vin){
        return new ArrayList<>();
    }

    default PageResult<GpsSource> pageByVin(String vin, Page page){
        return new PageResult<>(new ArrayList<>(),0);
    }

    default List<List<GpsSource>> listSplit(String vin,Integer num,Date startTime,Date endTime){
        return new ArrayList<>();
    }
}
