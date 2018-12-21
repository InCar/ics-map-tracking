package com.incarcloud.business.source;

public class GpsSource {
    protected String vin;

    protected Double lng;

    protected Double lat;

    protected Float direction;

    public GpsSource(String vin, Double lng, Double lat, Float direction) {
        this.vin = vin;
        this.lng = lng;
        this.lat = lat;
        this.direction = direction;
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
}
