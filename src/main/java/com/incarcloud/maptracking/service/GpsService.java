package com.incarcloud.maptracking.service;

import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;
import com.incarcloud.maptracking.data.GpsSplitSummary;
import com.incarcloud.maptracking.source.GpsSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface GpsService {
    /**
     * 查询
     * @param vin
     * @param startTime
     * @param endTime
     * @return
     */
    List<GpsSource> list(String vin, Date startTime, Date endTime);

    /**
     * 查询(分页)
     * @param vin
     * @param startTime
     * @param endTime
     * @param page
     * @return
     */
    PageResult<GpsSource> page(String vin, Date startTime, Date endTime, Page page);

    /**
     * 查询Gps分段信息明细
     * @param vin
     * @param num
     * @param startTime
     * @param endTime
     * @param order
     * @return
     */
    List<List<GpsSource>> listSplit(String vin,Integer num,Date startTime,Date endTime,Integer order);

    /**
     * 查询Gps分段信息摘要
     * @param vin
     * @param num
     * @param startTime
     * @param endTime
     * @param order
     * @return
     */
    List<GpsSplitSummary> listSplitSummary(String vin, Integer num, Date startTime, Date endTime, Integer order);
}
