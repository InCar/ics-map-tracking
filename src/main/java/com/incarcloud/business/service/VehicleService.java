package com.incarcloud.business.service;

import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.source.VehicleSource;

import java.util.ArrayList;
import java.util.List;

public interface VehicleService {
    default List<VehicleSource> listByVin(String vin){
        return new ArrayList<>();
    }

    default PageResult<VehicleSource> pageByVin(String vin, Page page){
        return new PageResult<>(new ArrayList<>(),0);
    }
}
