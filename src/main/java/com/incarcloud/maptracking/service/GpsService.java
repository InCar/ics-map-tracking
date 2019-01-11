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
     * @param vin vin码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<GpsSource> list(String vin, Date startTime, Date endTime);

    /**
     * 查询(分页)
     * @param vin vin码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 分页参数
     * @return
     */
    PageResult<GpsSource> page(String vin, Date startTime, Date endTime, Page page);

    /**
     * 查询Gps分段信息明细
     * @param vin vin码
     * @param num 查询轨迹段数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param gpsSplitTimeMills gps分段时间(毫秒)
     * @param order 排序(1:正序;2:逆序)
     * @return
     */
    List<List<GpsSource>> listSplit(String vin,Integer num,Date startTime,Date endTime,Long gpsSplitTimeMills,Integer order);

    /**
     * 查询Gps分段信息摘要
     * @param vin vin码
     * @param num 查询轨迹段数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param gpsSplitTimeMills gps分段时间(毫秒)
     * @param order 排序(1:正序;2:逆序)
     * @return
     */
    List<GpsSplitSummary> listSplitSummary(String vin, Integer num, Date startTime, Date endTime,Long gpsSplitTimeMills, Integer order);
}
