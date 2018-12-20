package com.incarcloud.business.service;

import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.source.GpsSource;

import java.util.ArrayList;
import java.util.List;

public interface GpsService {
    default List<GpsSource> listByGprscode(String gprsCode){
        return new ArrayList<>();
    }

    default PageResult<GpsSource> pageByGprscode(String gprsCode, Page page){
        return new PageResult<>(new ArrayList<>(),0);
    }
}
