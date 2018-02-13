package com.Emergency.Patient.Models;

/**
 * Created by kamlesh on 11-02-2018.
 */

public class Locate {
    private double latitude;
    private double longitude;


    public Locate() {
        super();
    }

    public Locate(double latitude, double longitude) {
       this.latitude=latitude;
       this.longitude=longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
