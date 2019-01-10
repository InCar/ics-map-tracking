package com.incarcloud.maptracking.controller;

import com.incarcloud.skeleton.anno.ICSAutowire;
import com.incarcloud.skeleton.anno.ICSController;
import com.incarcloud.skeleton.anno.ICSRequestMapping;
import com.incarcloud.skeleton.anno.ICSRequestParam;
import com.incarcloud.skeleton.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.skeleton.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;
import com.incarcloud.skeleton.message.JsonMessage;
import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;
import com.incarcloud.maptracking.service.VehicleService;
import com.incarcloud.maptracking.source.VehicleSource;

import java.util.List;

@ICSController
@ICSRequestMapping(value = "/vehicle")
public class VehicleController extends BaseComponent {
    @ICSAutowire
    private VehicleService vehicleService;

    @ICSRequestMapping(value = "/list", method = ICSHttpRequestMethodEnum.GET)
    public JsonMessage<List<VehicleSource>> list(
            @ICSRequestParam(required = false, value = "vin")
                    String vin) {
        return JsonMessage.success(vehicleService.listByVin(vin));
    }

    @ICSRequestMapping(value = "/page", method = ICSHttpRequestMethodEnum.GET)
    public JsonMessage<PageResult<VehicleSource>> page(
            @ICSRequestParam(required = false, value = "vin") String vin,
            @ICSRequestParam(required = false, value = "pageNum",defaultValue = "1") Integer pageNum,
            @ICSRequestParam(required = false, value = "pageSize",defaultValue = "10") Integer pageSize) {
        return JsonMessage.success(vehicleService.pageByVin(vin, new Page(pageNum, pageSize)));
    }


}
