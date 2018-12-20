package com.incarcloud.base.page;

import java.util.List;

public class PageResult<T> {
    private List<T> dataList;
    private int total;

    public PageResult(List<T> dataList, int total) {
        this.dataList = dataList;
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
