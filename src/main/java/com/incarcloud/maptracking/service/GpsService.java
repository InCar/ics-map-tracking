package com.incarcloud.maptracking.service;

import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;
import com.incarcloud.maptracking.data.GpsSplitSummary;
import com.incarcloud.maptracking.source.GpsSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface GpsService {
    default List<GpsSource> listByVin(String vin,Date startTime, Date endTime){
        return new ArrayList<>();
    }

    default PageResult<GpsSource> pageByVin(String vin,Date startTime, Date endTime, Page page){
        return new PageResult<>(new ArrayList<>(),0);
    }

    default List<List<GpsSource>> listSplit(String vin,Integer num,Date startTime,Date endTime,Integer order){
        return new ArrayList<>();
    }

    default List<GpsSplitSummary> listSplitSummary(String vin, Integer num, Date startTime, Date endTime, Integer order){
        return new ArrayList<>();
    }
}
