package com.dat.stormy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by dat on 26/06/2015.
 * api key = http://api.openweathermap.org/data/2.5/weather?lat=37.8267&lon=-122.423&units=metric
 */
public class CurrentWeatherOpen {
    private long  mSunRise;        //unix, UTC
    private long  mSunSet;         //unix, UTC

    private String  mWeatherDescription;
    private String  mWeatherIcon;    //name of png file

    private double  mTemperature;    //C
    private double  mTemperatureMax; //C
    private double  mTemperatureMin; //C
    private double  mPressure;       //hPa
    private double  mSeaLevel;       //hPa   Atmospheric pressure on the sea level
    private double  mGroundLevel;    //hPa   Atmospheric pressure on the ground level
    private double  mHumidity;       // do am

    private double   mWindSpeed;      //meter/sec
    private double   mWindDeg;        //degrees (meteorological)  wind direction
    private double   mWindGust;       //meter/sec     gio Giat

    private double   mCloud;          //% cloudiness

    private double  mRain;            //mm   Precipitation volume for last 3 hours
    private long    mTime;           //~dt in Api unix, UTC    // do for weather in day, week

    private String  mLocation;


    // for weather day API


    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public double getSunRise() {
        return mSunRise;
    }

    public void setSunRise(long sunRise) {
        mSunRise = sunRise;
    }

    public double getSunSet() {
        return mSunSet;
    }

    public void setSunSet(long sunSet) {
        mSunSet = sunSet;
    }

    public String getWeatherDescription() {
        return mWeatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        mWeatherDescription = weatherDescription;
    }

    public String getWeatherIcon() {
        return mWeatherIcon;
    }

    public int getWeatherIconId(){
        if(mWeatherIcon.equals("01d"))
            return R.drawable.clear_sky_d;
        else if(mWeatherIcon.equals("01n"))
            return R.drawable.clear_sky_n;
        else if(mWeatherIcon.equals("02d"))
            return R.drawable.few_clouds_d;
        else if(mWeatherIcon.equals("02n"))
            return R.drawable.few_clouds_n;
        else if(mWeatherIcon.equals("03d"))
            return R.drawable.scattered_clouds;
        else if(mWeatherIcon.equals("03n"))
            return R.drawable.scattered_clouds;
        else if(mWeatherIcon.equals("04d"))
            return R.drawable.broken_clouds;
        else if(mWeatherIcon.equals("04n"))
            return R.drawable.broken_clouds;
        else if(mWeatherIcon.equals("09d"))
            return R.drawable.shower_rain;
        else if(mWeatherIcon.equals("09n"))
            return R.drawable.shower_rain;
        else if(mWeatherIcon.equals("10d"))
            return R.drawable.rain_d;
        else if(mWeatherIcon.equals("10n"))
            return R.drawable.rain_n;
        else if(mWeatherIcon.equals("11d"))
            return R.drawable.thunderstorm;
        else if(mWeatherIcon.equals("11n"))
            return R.drawable.thunderstorm;
        else if(mWeatherIcon.equals("13d"))
            return R.drawable.snow;
        else if(mWeatherIcon.equals("13n"))
            return R.drawable.snow;
        else if(mWeatherIcon.equals("50d"))
            return R.drawable.mist;
        else if(mWeatherIcon.equals("50n"))
            return R.drawable.mist;
        return  R.drawable.clear_sky_d;
    }

    public void setWeatherIcon(String weatherIcon) {
        mWeatherIcon = weatherIcon;
    }

    public int getTemperature() {
        return (int)mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public int getTemperatureMax() {
        return (int)mTemperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public int getTemperatureMin() {
        return (int)mTemperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        mTemperatureMin = temperatureMin;
    }

    public double getPressure() {
        return mPressure;
    }

    public void setPressure(double pressure) {
        mPressure = pressure;
    }

    public double getSeaLevel() {
        return mSeaLevel;
    }

    public void setSeaLevel(double seaLevel) {
        mSeaLevel = seaLevel;
    }

    public double getGroundLevel() {
        return mGroundLevel;
    }

    public void setGroundLevel(double groundLevel) {
        mGroundLevel = groundLevel;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public String getWindSpeed() {
        return (int)mWindSpeed+"%";
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public double getWindDeg() {
        return mWindDeg;
    }

    public void setWindDeg(double windDeg) {
        mWindDeg = windDeg;
    }

    public double getWindGust() {
        return mWindGust;
    }

    public void setWindGust(double windGust) {
        mWindGust = windGust;
    }

    public double getCloud() {
        return mCloud;
    }

    public void setCloud(double cloud) {
        mCloud = cloud;
    }

    public double getRain() {
        return mRain;
    }

    public void setRain(double rain) {
        mRain = rain;
    }

    public String getTime() {
        SimpleDateFormat formater = new SimpleDateFormat("h:mm a");
        formater.setTimeZone(TimeZone.getTimeZone("unix, UTC"));
        Date dateTime = new Date(mTime*1000);
        String timeString = formater.format(dateTime);
        return timeString;
    }

    public void setTime(long time) {
        mTime = time;
    }
}
