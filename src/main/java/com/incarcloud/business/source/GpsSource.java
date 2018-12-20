package com.incar.business.source;

public class GpsSource {
    protected String gprscode;

    protected Double lng;

    protected Double lat;

    public GpsSource(String gprscode, Double lng, Double lat) {
        this.gprscode = gprscode;
        this.lng = lng;
        this.lat = lat;
    }

    public String getGprscode() {
        return gprscode;
    }

    public void setGprscode(String gprscode) {
        this.gprscode = gprscode;
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
