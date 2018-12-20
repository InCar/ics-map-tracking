package com.incar.business.source;

public class VehicleSource {
    protected String gprscode;

    protected String plateNo;

    public VehicleSource(String gprscode, String plateNo) {
        this.gprscode = gprscode;
        this.plateNo = plateNo;
    }

    public String getGprscode() {
        return gprscode;
    }

    public void setGprscode(String gprscode) {
        this.gprscode = gprscode;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }
}
