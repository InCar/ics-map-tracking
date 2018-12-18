package com.incar.business.controller;

import com.incar.base.handler.dynamicrequest.anno.ICSAutowire;
import com.incar.base.handler.dynamicrequest.anno.ICSController;
import com.incar.base.handler.dynamicrequest.anno.ICSRequestMapping;
import com.incar.base.handler.dynamicrequest.anno.ICSRequestParam;
import com.incar.base.handler.dynamicrequest.component.BaseComponent;
import com.incar.base.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;
import com.incar.base.page.Page;
import com.incar.base.page.PageResult;
import com.incar.business.service.VehicleService;
import com.incar.business.source.VehicleSource;

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
            @ICSRequestParam(required = true, value = "pageNum") int pageNum,
            @ICSRequestParam(required = true, value = "pageSize") int pageSize) {
        return vehicleService.pageByGprscode(gprscode, new Page(pageNum, pageSize));
    }
}
