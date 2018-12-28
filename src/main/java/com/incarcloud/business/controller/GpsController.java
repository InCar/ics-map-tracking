package com.incarcloud.business.controller;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.anno.ICSController;
import com.incarcloud.base.anno.ICSRequestMapping;
import com.incarcloud.base.anno.ICSRequestParam;
import com.incarcloud.base.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.base.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;
import com.incarcloud.base.message.JsonMessage;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.data.GpsSplitSummary;
import com.incarcloud.business.service.GpsService;
import com.incarcloud.business.source.GpsSource;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@ICSController
@ICSRequestMapping(value = "/gps")
public class GpsController extends BaseComponent{
    @ICSAutowire
    private GpsService gpsService;
    @ICSRequestMapping(value = "/list",method = ICSHttpRequestMethodEnum.GET)
    public JsonMessage<List<GpsSource>> list(
            @ICSRequestParam(required = false,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime){
        return JsonMessage.success(gpsService.listByVin(vin,startTime,endTime));
    }

    @ICSRequestMapping(value = "/page",method = ICSHttpRequestMethodEnum.GET)
    public JsonMessage<PageResult<GpsSource>> page(
            @ICSRequestParam(required = false,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime,
            @ICSRequestParam(required = false, value = "pageNum",defaultValue = "1") Integer pageNum,
            @ICSRequestParam(required = false, value = "pageSize",defaultValue = "10") Integer pageSize){
        return JsonMessage.success(gpsService.pageByVin(vin,startTime,endTime,new Page(pageNum,pageSize)));
    }

    @ICSRequestMapping(value = "/listSplit",method = ICSHttpRequestMethodEnum.GET)
    public JsonMessage<List<List<GpsSource>>> listSplit(
            @ICSRequestParam(required = true,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "num",defaultValue = Integer.MAX_VALUE+"") Integer num,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime,
            @ICSRequestParam(required = false,value = "order",defaultValue = "2")Integer order
            ){
        List<List<GpsSource>> resultList= gpsService.listSplit(vin, num, startTime, endTime,order);
        //6、如果是逆序,则需要倒转每一个数据集的内容
        if(order==2){
            resultList.forEach(e-> Collections.reverse(e));
        }
        return JsonMessage.success(resultList);
    }

    @ICSRequestMapping(value = "/listSplitSummary",method = ICSHttpRequestMethodEnum.GET)
    public JsonMessage<List<GpsSplitSummary>> listSplitSummary(
            @ICSRequestParam(required = true,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "num",defaultValue = Integer.MAX_VALUE+"") Integer num,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime,
            @ICSRequestParam(required = false,value = "order",defaultValue = "2")Integer order
    ){
        List<GpsSplitSummary> dataList= gpsService.listSplitSummary(vin, num, startTime, endTime,order);
        return JsonMessage.success(dataList);
    }
}
