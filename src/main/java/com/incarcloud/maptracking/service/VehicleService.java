package com.incarcloud.maptracking.service;

import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;
import com.incarcloud.maptracking.source.VehicleSource;

import java.util.ArrayList;
import java.util.List;

public interface VehicleService {
    /**
     * 查询
     * @param vin
     * @return
     */
    List<VehicleSource> list(String vin);

    /**
     * 查询(分页)
     * @param vin
     * @param page
     * @return
     */
    PageResult<VehicleSource> page(String vin, Page page);
}
