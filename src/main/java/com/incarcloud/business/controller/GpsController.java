package com.incar.business.controller;

import com.incar.base.anno.ICSAutowire;
import com.incar.base.anno.ICSController;
import com.incar.base.anno.ICSRequestMapping;
import com.incar.base.anno.ICSRequestParam;
import com.incar.base.handler.dynamicrequest.component.BaseComponent;
import com.incar.base.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;
import com.incar.base.page.Page;
import com.incar.base.page.PageResult;
import com.incar.business.service.GpsService;
import com.incar.business.source.GpsSource;

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
        return gpsService.listByGprscode(gprsCode);
    }

    @ICSRequestMapping(value = "/page",method = ICSHttpRequestMethodEnum.GET)
    public PageResult<GpsSource> page(
            @ICSRequestParam(required = false,value = "gprsCode") String gprsCode,
            @ICSRequestParam(required = true, value = "pageNum") Integer pageNum,
            @ICSRequestParam(required = true, value = "pageSize") Integer pageSize){
        return gpsService.pageByGprscode(gprsCode,new Page(pageNum,pageSize));
    }
}
