package com.dat.stormy.model;

import java.io.Serializable;

/**
 * Created by dat on 13/07/2015.
 */
public class Locate implements Serializable{
    private String mAddress = "Address";
    private double mLongtitude = 10.562;
    private double mLatitude = 106.125;

    public Locate(String address, double latitude, double longtitude){
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongtitude = longtitude;
    }

    public String getAddress() {
        return mAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongtitude() {
        return mLongtitude;
    }

}
