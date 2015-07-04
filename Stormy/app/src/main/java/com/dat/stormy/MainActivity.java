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
import java.util.ArrayList;
import java.util.Date;
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

    @InjectView(R.id.GoButton) ImageView mGoButton;
    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureValue;
    @InjectView(R.id.humidityValue) TextView mHumidityLabel;
    @InjectView(R.id.precipValue) TextView mPercipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.LocationEditText) TextView mLocationValue;
    @InjectView(R.id.locationLabel) TextView mLocationlabel;
    @InjectView(R.id.Chart)    RelativeLayout mChart;

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

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               GoOnClick();
            }
        });
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

    private void GoOnClick(){
        String namePlace = mLocationValue.getText().toString();
        if(namePlace.equals(""))
            Toast.makeText(MainActivity.this,"Please input name of place!",Toast.LENGTH_LONG).show();
        else {
            final String key = String.format(GET_LONG_LAT, namePlace).replace(" ", "%20");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new LoadWebBody().execute(CONDITION_ADDRESS, key);
                }
            });
        }
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
                    CreateChart(weatherForFiveDay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{

            }
           /*try {
               final CurrentWeatherOpen weatherOpen = ExtractValueFromJson(s);
               Log.d(TAG,"LoadWebBody : temper: "+weatherOpen.getTemperature());
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       updateDisplayOpen(weatherOpen);
                   }
               });

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    }

    private void updateDisplayOpen(CurrentWeatherOpen weatherOpen) {
        mTemperatureValue.setText(weatherOpen.getTemperature() + "");
        mTimeLabel.setText("At "+weatherOpen.getTime()+" it will be");
        mLocationlabel.setText(weatherOpen.getLocation());
        mHumidityLabel.setText(weatherOpen.getHumidity() + "");
        mPercipValue.setText(weatherOpen.getRain() + "");
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

    //Display result
    private void updateDisplay(CurrentWeather currentWeather) {
               mTemperatureValue.setText(currentWeather.getTemperature() + "");
               mTimeLabel.setText("At "+currentWeather.getFormattedTime()+" it will be");
               mLocationlabel.setText(currentWeather.getTimeZone());
               mHumidityLabel.setText(currentWeather.getHumidity() + "");
               mPercipValue.setText(currentWeather.getPrecipChance() + "");
               mSummaryLabel.setText(currentWeather.getSummary());
               mIconImageView.setImageDrawable(getResources().getDrawable(currentWeather.getIconId()));
    }

    public CurrentWeatherOpen ExtractValueFromJson(String json) throws JSONException {
        CurrentWeatherOpen currentWeatherOpen = new CurrentWeatherOpen();
        JSONObject jsonObject = new JSONObject(json);
        JSONObject sys = jsonObject.has("sys")?jsonObject.getJSONObject("sys"):null;
        JSONArray weather = jsonObject.has("weather")? jsonObject.getJSONArray("weather"):null;
        JSONObject weatherElement = weather!=null? weather.getJSONObject(0):null;
        JSONObject main = jsonObject.has("main")? jsonObject.getJSONObject("main"):null;
        JSONObject wind = jsonObject.has("wind")?jsonObject.getJSONObject("wind"):null;
        JSONObject cloud = jsonObject.has("clouds")?jsonObject.getJSONObject("clouds"):null;
        //JSONObject rain = jsonObject.getJSONObject("rain");

        if(sys!=null) {
            currentWeatherOpen.setSunRise(sys.getLong("sunrise"));
            currentWeatherOpen.setSunSet (sys.getLong("sunset"));
        }
        if(weatherElement!=null) {
            currentWeatherOpen.setWeatherDescription (weatherElement.getString("description"));
            currentWeatherOpen.setWeatherIcon (weatherElement.getString("icon"));
        }
        if(main!=null){
            currentWeatherOpen.setTemperature (main.getDouble("temp"));
            currentWeatherOpen.setTemperatureMax (main.getDouble("temp_max"));
            currentWeatherOpen.setTemperatureMin (main.getDouble("temp_min"));
            currentWeatherOpen.setPressure (main.getDouble("pressure"));
            currentWeatherOpen.setHumidity (main.getDouble("humidity"));
//            currentWeatherOpen.setSeaLevel (main.getDouble("sea_level"));
  //          currentWeatherOpen.setGroundLevel (main.getDouble("grnd_level"));
        }
        if(wind!=null) {
            currentWeatherOpen.setWindSpeed (wind.getDouble("speed"));
            currentWeatherOpen.setWindDeg (wind.getDouble("deg"));
//            currentWeatherOpen.setWindGust (wind.getDouble("gust"));
        }
        if(cloud!=null)
            currentWeatherOpen.setCloud (cloud.getDouble("all"));
        //if(rain!=null)
        //    currentWeatherOpen.setRain (rain.getDouble("3h"));
        currentWeatherOpen.setTime ( jsonObject.getLong("dt"));
        currentWeatherOpen.setLocation(jsonObject.getString("name"));
        return currentWeatherOpen;
    }


    private WeatherForFiveDay ExtractValueFromJsonFiveDay(String json) throws JSONException {
        WeatherForFiveDay weatherForFiveDay = new WeatherForFiveDay();
        List dataSets = new ArrayList<FiveDayDataSet>();

        JSONObject jsonObject = new JSONObject(json);

        try {
            if (jsonObject != null) {
                if (jsonObject.has("name")) {
                    weatherForFiveDay.setLocation(jsonObject.getString("name"));
                }
                if (jsonObject.has("country")) {
                    weatherForFiveDay.setCountry(jsonObject.getString("country"));
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
        weatherForFiveDay.setFiveDayDataSets(dataSets);
        return weatherForFiveDay;
    }

    private void CreateChart(WeatherForFiveDay weatherForFiveDay) {
        List dataSets = weatherForFiveDay.getFiveDayDataSets();
        int size = dataSets.size();
        float[] temperatures = new float[5];
        Date now = new Date();
        int count=0;
        for(int i=0;i<size;i++){
            FiveDayDataSet temp = (FiveDayDataSet)dataSets.get(i);
            if(Math.abs(temp.getTime().getHours() - now.getHours()) <= 1) {
                temperatures[count++] = temp.getTemperature();
                String current =mSummaryLabel.getText().toString();
                mSummaryLabel.setText(current +temp.getTime().getDay()+" ");
            }
        }
        DrawChart drawChart = new DrawChart(mChart,MainActivity.this,temperatures);

         drawChart.StartDraw();
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

    private void AlertDialog(){
        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Confirm Delete...");
                alertDialog.setMessage("Are you sure you want delete this?");
                alertDialog.setIcon(R.drawable.ic_launcher);
                alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
            }
        }, 100);
    }

}
