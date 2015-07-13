package com.dat.stormy.model;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Phong on 04/07/2015.
 */
public class GetXml extends Activity{
    private Context mContext;

    public final String GET_LONG_LAT = "http://maps.googleapis.com/maps/api/geocode/json?address=%1$s&sensor=true";
    public final String FORECAST = "http://api.openweathermap.org/data/2.5/forecast?%1$s&units=metric";

    public GetXml(Context context){
        this.mContext = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return  isAvailable;
    }

    public String getXmlFromUrl(String urlString) {

        String responseBody ="Wrong";
        if(isNetworkAvailable()) {
            DefaultHttpClient http = new DefaultHttpClient();
            HttpGet httpMethod = new HttpGet();
            try {
                httpMethod.setURI(new URI(urlString));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            HttpResponse response = null;
            try {
                response = http.execute(httpMethod);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int responseCode = response.getStatusLine().getStatusCode();
            switch (responseCode) {
                case 200:
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try {
                            responseBody = EntityUtils.toString(entity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
        else{
            Toast.makeText(mContext, "Network is not available!", Toast.LENGTH_LONG).show();
        }

        return responseBody;
    }

    public Location getCurrentGPS(){

        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        String provider;
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            provider = LocationManager.GPS_PROVIDER;
        else{
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            crit.setPowerRequirement(Criteria.POWER_LOW);
            provider = lm.getBestProvider(crit,true);
        }
        final Location[] currentLocation = {lm.getLastKnownLocation(provider)};
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation[0] = location;
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
            }
        };
        lm.requestLocationUpdates(provider, 2000, 10, locationListener);
        return currentLocation[0];
    }

    public String extractLongLatFromAddress(String json) {
        String outPut="";
        try {
            JSONObject body = new JSONObject(json);
            JSONArray result = body.getJSONArray("results");
            JSONObject temp = result.getJSONObject(0);
            JSONObject geo =temp.getJSONObject("geometry");
            JSONObject location = geo.getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            outPut = "lat="+lat+"&lon="+lng;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outPut;
    }

    public WeatherForFiveDay ExtractValueFromJsonFiveDay(String json) throws JSONException {
        WeatherForFiveDay weatherForFiveDay = new WeatherForFiveDay();
        List<FiveDayDataSet> dataSets = new ArrayList<FiveDayDataSet>();

        JSONObject jsonObject = new JSONObject(json);

        try {
            if (jsonObject != null) {
                if (jsonObject.has("city")) {
                    weatherForFiveDay.setLocation(jsonObject.getJSONObject("city").getString("name"));
                    weatherForFiveDay.setCountry(jsonObject.getJSONObject("city").getString("country"));
                }

                JSONArray jsonArray = jsonObject.getJSONArray("list");

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        FiveDayDataSet data = new FiveDayDataSet();

                        if (object.has("dt_txt")) {
                            String date = object.getString("dt_txt");
                            SimpleDateFormat formater = new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            int hour = formater.parse(date).getHours();
                            int nowHour = new Date().getHours();
                            if(Math.abs(hour - nowHour) > 1)
                                continue;
                            else data.setTime(date);
                        }

                        if (object.has("main")) {
                            JSONObject main = object.getJSONObject("main");
                            data.setTemperature(main.getDouble("temp"));
                            data.setTemperatureMin(main.getDouble("temp_min"));
                            data.setTemperatureMax(main.getDouble("temp_max"));
                            data.setPressure(main.getDouble("pressure"));
                            data.setSeaLevel(main.getDouble("sea_level"));
                            data.setGroundLevel(main.getDouble("grnd_level"));
                            data.setHumidity(main.getDouble("humidity"));
                        }
                        if (object.has("weather")) {
                            JSONArray weathers = object.getJSONArray("weather");
                            JSONObject weather = weathers.getJSONObject(0);
                            data.setWeatherDescription(weather.getString("description"));
                            data.setWeatherIcon(weather.getString("icon"));
                        }
                        if (object.has("clouds")) {
                            if(object.getJSONObject("clouds").has("all"))
                                data.setCloud(object.getJSONObject("clouds").getDouble("all"));
                        }
                        if (object.has("wind")) {
                            JSONObject wind = object.getJSONObject("wind");
                            if(wind.has("speed"))
                                data.setWindSpeed(wind.getDouble("speed"));
                            if(wind.has("deg"))
                            data.setWindDeg(wind.getDouble("deg"));
                        }
                        if(object.has("rain")){
                            if(object.getJSONObject("rain").has("3h"))
                                data.setRain(object.getJSONObject("rain").getDouble("3h"));
                        }
                        dataSets.add(data);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
       /* int now = new Date().getHours();
        List<FiveDayDataSet> dataSetsFormated = new ArrayList<FiveDayDataSet>();
        for(int i=0;i<dataSets.size();i++){
            if(Math.abs(dataSets.get(i).getTime().getHours() - now) <= 1){
                dataSetsFormated.add(dataSets.get(i));
            }
        }
        weatherForFiveDay.setFiveDayDataSets(dataSetsFormated);*/
        weatherForFiveDay.setFiveDayDataSets(dataSets);
        return weatherForFiveDay;
    }

}
