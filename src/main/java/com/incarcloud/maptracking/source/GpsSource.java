package com.incarcloud.maptracking.source;

import java.util.Date;

public class GpsSource {
    protected String vin;

    protected Double lng;

    protected Double lat;

    protected Float direction;

    protected Date time;

    protected Integer speed;


    public GpsSource() {
    }

    public GpsSource(String vin, Double lng, Double lat, Float direction, Date time,Integer speed) {
        this.vin = vin;
        this.lng = lng;
        this.lat = lat;
        this.direction = direction;
        this.time=time;
        this.speed=speed;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Float getDirection() {
        return direction;
    }

    public void setDirection(Float direction) {
        this.direction = direction;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
