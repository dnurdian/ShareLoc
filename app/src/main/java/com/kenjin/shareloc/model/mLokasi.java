package com.kenjin.shareloc.model;

import java.io.Serializable;

/**
 * Created by kenjin on 24/01/18.
 */

public class mLokasi implements Serializable {
    private double Latitude;
    private double Longitude;
    private  String LocationName;
    private  String DateTaken;

    public mLokasi(double latitude, double longitude, String locationName,
                   String dateTaken) {
        Latitude = latitude;
        Longitude = longitude;
        LocationName = locationName;
        DateTaken = dateTaken;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getDateTaken() {
        return DateTaken;
    }

    public void setDateTaken(String dateTaken) {
        DateTaken = dateTaken;
    }

}
