package com.incarcloud.business.controller;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.anno.ICSController;
import com.incarcloud.base.anno.ICSRequestMapping;
import com.incarcloud.base.anno.ICSRequestParam;
import com.incarcloud.base.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.base.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
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
            @ICSRequestParam(required = false,value = "vin")
                    String vin){
        return gpsService.listByVin(vin);
    }

    @ICSRequestMapping(value = "/page",method = ICSHttpRequestMethodEnum.GET)
    public PageResult<GpsSource> page(
            @ICSRequestParam(required = false,value = "vin") String vin,
            @ICSRequestParam(required = true, value = "pageNum") Integer pageNum,
            @ICSRequestParam(required = true, value = "pageSize") Integer pageSize){
        return gpsService.pageByVin(vin,new Page(pageNum,pageSize));
    }

    @ICSRequestMapping(value = "/listSplit",method = ICSHttpRequestMethodEnum.GET)
    public List<List<GpsSource>> listSplit(
            @ICSRequestParam(required = false,value = "vin") String vin,
            @ICSRequestParam(required = false,value = "num") Integer num,
            @ICSRequestParam(required = false,value = "startTime")Date startTime,
            @ICSRequestParam(required = false,value = "endTime")Date endTime
            ){
                num=num==null?5:num;
        return gpsService.listSplit(vin, num, startTime, endTime);
    }
}
