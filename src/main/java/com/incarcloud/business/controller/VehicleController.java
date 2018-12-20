package com.incarcloud.business.controller;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.anno.ICSController;
import com.incarcloud.base.anno.ICSRequestMapping;
import com.incarcloud.base.anno.ICSRequestParam;
import com.incarcloud.base.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.base.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.service.VehicleService;
import com.incarcloud.business.source.VehicleSource;

import java.util.List;

@ICSController
@ICSRequestMapping(value = "/vehicle")
public class VehicleController extends BaseComponent {
    @ICSAutowire
    private VehicleService vehicleService;

    @ICSRequestMapping(value = "/list", method = ICSHttpRequestMethodEnum.GET)
    public List<VehicleSource> list(
            @ICSRequestParam(required = false, value = "gprscode")
                    String gprsCode) {
        return vehicleService.listByGprscode(gprsCode);
    }

    @ICSRequestMapping(value = "/page", method = ICSHttpRequestMethodEnum.GET)
    public PageResult<VehicleSource> page(
            @ICSRequestParam(required = false, value = "gprscode") String gprscode,
            @ICSRequestParam(required = true, value = "pageNum") Integer pageNum,
            @ICSRequestParam(required = true, value = "pageSize") Integer pageSize) {
        return vehicleService.pageByGprscode(gprscode, new Page(pageNum, pageSize));
    }
}
