package com.incarcloud.business.controller;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.anno.ICSController;
import com.incarcloud.base.anno.ICSRequestMapping;
import com.incarcloud.base.anno.ICSRequestParam;
import com.incarcloud.base.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.base.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.data.GpsSplitSummary;
import com.incarcloud.business.service.GpsService;
import com.incarcloud.business.source.GpsSource;

import java.util.Date;
import java.util.List;

@ICSController
@ICSRequestMapping(value = "/gps")
public class GpsController extends BaseComponent{
    @ICSAutowire
    private GpsService gpsService;
    @ICSRequestMapping(value = "/list",method = ICSHttpRequestMethodEnum.GET)
    public List<GpsSource> list(
            @ICSRequestParam(required = false,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime){
        return gpsService.listByVin(vin,startTime,endTime);
    }

    @ICSRequestMapping(value = "/page",method = ICSHttpRequestMethodEnum.GET)
    public PageResult<GpsSource> page(
            @ICSRequestParam(required = false,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime,
            @ICSRequestParam(required = false, value = "pageNum",defaultValue = "1") Integer pageNum,
            @ICSRequestParam(required = false, value = "pageSize",defaultValue = "10") Integer pageSize){
        return gpsService.pageByVin(vin,startTime,endTime,new Page(pageNum,pageSize));
    }

    @ICSRequestMapping(value = "/listSplit",method = ICSHttpRequestMethodEnum.GET)
    public List<List<GpsSource>> listSplit(
            @ICSRequestParam(required = true,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "num",defaultValue = Integer.MAX_VALUE+"") Integer num,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime,
            @ICSRequestParam(required = false,value = "order",defaultValue = "2")Integer order
            ){
        return gpsService.listSplit(vin, num, startTime, endTime,order);
    }

    @ICSRequestMapping(value = "/listSplitSummary",method = ICSHttpRequestMethodEnum.GET)
    public List<GpsSplitSummary> listSplitSummary(
            @ICSRequestParam(required = true,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "num",defaultValue = Integer.MAX_VALUE+"") Integer num,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime,
            @ICSRequestParam(required = false,value = "order",defaultValue = "2")Integer order
    ){
        List<GpsSplitSummary> dataList= gpsService.listSplitSummary(vin, num, startTime, endTime,order);
        return dataList;
    }
}
