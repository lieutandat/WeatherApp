package com.dat.stormy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dat on 01/07/2015.
 */
public class WeatherForFiveDay {
    //object -> city (name, country, coord, populcation)
    private String  mLocation;
    private String mCountry;
    private List<FiveDayDataSet> mFiveDayDataSets;

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public List<FiveDayDataSet> getFiveDayDataSets() {
        return mFiveDayDataSets;
    }

    public void setFiveDayDataSets(List<FiveDayDataSet> fiveDayDataSets) {
        mFiveDayDataSets = fiveDayDataSets;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }
}
