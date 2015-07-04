package com.dat.stormy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity {

    final String TAG = MainActivity.class.getSimpleName();
    final String AIP_KEY = "55e90b435e00c7ee1ba7b1a4c2448c7a";
    final String GET_LONG_LAT = "http://maps.googleapis.com/maps/api/geocode/json?address=%1$s&sensor=true";
    final String FORECAST = "http://api.openweathermap.org/data/2.5/forecast?%1$s&units=metric";
            //"http://api.openweathermap.org/data/2.5/weather?%1$s&units=metric";//"https://api.forecast.io/forecast/%1$s/%2$s";

    final String CONDITION_ADDRESS = "ADDRESS";
    final String CONDITION_FORECAST = "FORECAST";

    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureValue;
    @InjectView(R.id.humidityValue) TextView mHumidityLabel;
    @InjectView(R.id.precipValue) TextView mPercipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.locationLabel) TextView mLocationlabel;
    @InjectView(R.id.Chart)   LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        Location currentLocation = GetCurrentGPS();
        double latitude ;
        double longtitude;

        if(currentLocation==null){
            latitude =  10.7568441;//37.8267;
            longtitude = 106.6491496;//-122.423;
        } else {
            latitude = currentLocation.getLatitude();// 10.7568441;//37.8267;
            longtitude = currentLocation.getLongitude();// 106.6491496;//-122.423;
        }

        GetForeCast(latitude,longtitude);
    }

    // get GPS Long/Lat at Current Place
    private Location GetCurrentGPS(){

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider;
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            provider = LocationManager.GPS_PROVIDER;
        else{
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            crit.setPowerRequirement(Criteria.POWER_LOW);
            provider = lm.getBestProvider(crit,true);
        }
        Log.d(TAG,"Current Provider : "+provider);
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

    //Get the Forecast from API
    private void GetForeCast(double lat, double lng) { //
        final String forecastUrl = String.format(FORECAST,"lat="+lat+"&lon="+lng);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new LoadWebBody().execute(CONDITION_FORECAST,forecastUrl);
            }
        });
    }

    //get Long/Lat from name of place or city
    private String GetLongLatFromAddress(String json) {
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
        Log.d(TAG," GetLongLatFromAddress : "+outPut);
        return outPut;
    }

    //CalBack Get Json from web by API -> update View
    class LoadWebBody extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = ChoiceCondition(params[0],params[1]);
            return  getXmlFromUrl(url);
        }
        protected void onPostExecute(String s){
            Log.d(TAG,"LoadWebBody : "+ s);
            if(!s.equals("Wrong")) {
                try {
                    final WeatherForFiveDay weatherForFiveDay = ExtractValueFromJsonFiveDay(s);
                    Log.d(TAG, "LoadWebBody : " + weatherForFiveDay.getFiveDayDataSets().size());
                    updateDisplayOpen(weatherForFiveDay.getCountry() + "/" + weatherForFiveDay.getLocation(), weatherForFiveDay.getFiveDayDataSets().get(0));
                    CreateChart(weatherForFiveDay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateDisplayOpen(String location,FiveDayDataSet weatherOpen) {
        mTemperatureValue.setText(weatherOpen.getTemperature() + "");
        Date date = weatherOpen.getTime();
        String hour = new SimpleDateFormat("hh:mm a").format(date);
        mTimeLabel.setText("At "+date.getDate()+"/"+(date.getMonth()+1)
                +" "+ hour+" it will be");
        mLocationlabel.setText(location);
        mHumidityLabel.setText((int)weatherOpen.getHumidity() + " %");
        double rain = weatherOpen.getRain() * 100;
        mPercipValue.setText(((float)Math.round(rain)/100) + " mm");
        mSummaryLabel.setText(weatherOpen.getWeatherDescription());
        mIconImageView.setImageDrawable(getResources().getDrawable(weatherOpen.getWeatherIconId()));
    }

    private String ChoiceCondition(String condition, String param){
        if(condition.equals(CONDITION_FORECAST)) {
            Log.d(TAG," LoadWebBody : do condition forecast : "+param);
            return param;
        }
        else if(condition.equals(CONDITION_ADDRESS)){
            String json = getXmlFromUrl(param);
            Log.d(TAG," LoadWebBody : Do ConditionAddress");
            Log.d(TAG," LoadWebBody : json = "+json);
            String address = GetLongLatFromAddress(json);
            Log.d(TAG," LoadWebBody : address = "+address);
            return  String.format(FORECAST,address);
        }
        return " wrong ";
    }

    // Get Json
    private String getXmlFromUrl(String urlString) {
        Log.d(TAG," getXml : url " +urlString);
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
            Toast.makeText(MainActivity.this,"Network is not available!",Toast.LENGTH_LONG).show();
        }
        Log.d(TAG," getXmlFromUrl : "+responseBody);
        return responseBody;
    }

    private WeatherForFiveDay ExtractValueFromJsonFiveDay(String json) throws JSONException {
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
                            data.setCloud(object.getJSONObject("clouds").getDouble("all"));
                        }
                        if (object.has("wind")) {
                            JSONObject wind = object.getJSONObject("wind");
                            data.setWindSpeed(wind.getDouble("speed"));
                            data.setWindDeg(wind.getDouble("deg"));
                        }
                        if(object.has("rain")){
                            data.setRain(object.getJSONObject("rain").getDouble("3h"));
                        }
                        if (object.has("dt_txt")) {
                            data.setTime(object.getString("dt_txt"));
                        }
                        Log.d(TAG,i+" s ");
                        dataSets.add(data);

                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Date now = new Date();
        List<FiveDayDataSet> dataSetsFormated = new ArrayList<FiveDayDataSet>();
        for(int i=0;i<dataSets.size();i++){
            if(Math.abs(dataSets.get(i).getTime().getHours() - now.getHours()) <= 1){
                dataSetsFormated.add(dataSets.get(i));
            }
        }
        weatherForFiveDay.setFiveDayDataSets(dataSetsFormated);
        return weatherForFiveDay;
    }

    private void CreateChart(final WeatherForFiveDay weatherForFiveDay) {
        final List<FiveDayDataSet> dataSets = weatherForFiveDay.getFiveDayDataSets();

        float[] temperatures = new float[5];
        String[] labelTemperatures = new String[5];

        for(int i=0;i<dataSets.size();i++){
            FiveDayDataSet temp = dataSets.get(i);
            temperatures[i]= temp.getTemperature();
            labelTemperatures[i] = temp.getTime().getDate()+"/"+(temp.getTime().getMonth()+1);
            Log.d(TAG,"DrawChart: "+temperatures[i]+" " +labelTemperatures[i]);
        }
        DrawChart drawChart = new DrawChart(mChart,MainActivity.this,temperatures,labelTemperatures);

        drawChart.StartDraw();

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                updateDisplayOpen(weatherForFiveDay.getCountry()+"/"+weatherForFiveDay.getLocation(),dataSets.get(entry.getXIndex()));
                //Toast.makeText(MainActivity.this,"data : entry "+entry.toString()+" i "+i,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected() { }
        });

    }

    //Check Network working
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return  isAvailable;
    }
}
