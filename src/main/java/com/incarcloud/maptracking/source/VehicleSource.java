package com.incarcloud.maptracking.source;

public class VehicleSource {
    protected String vin;

    protected String plateNo;

    public VehicleSource(String vin, String plateNo) {
        this.vin = vin;
        this.plateNo = plateNo;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }
}
