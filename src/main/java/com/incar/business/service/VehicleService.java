package com.incar.business.service;

import com.incar.base.page.Page;
import com.incar.base.page.PageResult;
import com.incar.business.source.VehicleSource;

import java.util.ArrayList;
import java.util.List;

public interface VehicleService {
    default List<VehicleSource> listByGprscode(String gprsCode){
        return new ArrayList<>();
    }

    default PageResult<VehicleSource> pageByGprscode(String gprsCode, Page page){
        return new PageResult<>(new ArrayList<>(),0);
    }
}
