package com.incarcloud.maptracking.service;

import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;
import com.incarcloud.maptracking.source.VehicleSource;

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
