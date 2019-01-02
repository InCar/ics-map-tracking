package com.incarcloud.business.data;

import java.util.Date;

public class GpsSplitSummary {
    private Date startTime;
    private Date endTime;
    private int num;
    private double[] startPoint;
    private double[] endPoint;

    public GpsSplitSummary(Date startTime, Date endTime, int num, double[] startPoint, double[] endPoint) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.num = num;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double[] getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(double[] startPoint) {
        this.startPoint = startPoint;
    }

    public double[] getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(double[] endPoint) {
        this.endPoint = endPoint;
    }
}
