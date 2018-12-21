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

import java.util.List;

@ICSController
@ICSRequestMapping(value = "/gps")
public class GpsController extends BaseComponent{
    @ICSAutowire
    private GpsService gpsService;
    @ICSRequestMapping(value = "/list",method = ICSHttpRequestMethodEnum.GET)
    public List<GpsSource> list(
            @ICSRequestParam(required = false,value = "gprsCode")
                    String gprsCode){
        return gpsService.listByVin(gprsCode);
    }

    @ICSRequestMapping(value = "/page",method = ICSHttpRequestMethodEnum.GET)
    public PageResult<GpsSource> page(
            @ICSRequestParam(required = false,value = "gprsCode") String gprsCode,
            @ICSRequestParam(required = true, value = "pageNum") Integer pageNum,
            @ICSRequestParam(required = true, value = "pageSize") Integer pageSize){
        return gpsService.pageByVin(gprsCode,new Page(pageNum,pageSize));
    }
}
