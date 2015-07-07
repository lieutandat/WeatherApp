package com.dat.stormy.view;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dat.stormy.model.DrawChart;
import com.dat.stormy.model.FiveDayDataSet;
import com.dat.stormy.R;
import com.dat.stormy.model.GetXml;
import com.dat.stormy.model.WeatherForFiveDay;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
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
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity {

    final String TAG = MainActivity.class.getSimpleName();

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

    GetXml mGetXml = new GetXml(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        Location currentLocation = mGetXml.getCurrentGPS();// GetCurrentGPS();
        double latitude ;
        double longtitude;

        if(currentLocation==null){
            latitude =  10.7568441;
            longtitude = 106.6491496;
        } else {
            latitude = currentLocation.getLatitude();
            longtitude = currentLocation.getLongitude();
        }

        GetForeCast(latitude, longtitude);

    }

    //Get the Forecast from API
    private void GetForeCast(double lat, double lng) { //
        final String forecastUrl = String.format(mGetXml.FORECAST,"lat="+lat+"&lon="+lng);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new LoadWebBody().execute(CONDITION_FORECAST,forecastUrl);
            }
        });
    }

    //CalBack Get Json from web by API -> update View
    class LoadWebBody extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = ChoiceCondition(params[0],params[1]);
            return  mGetXml.getXmlFromUrl(url);
        }
        protected void onPostExecute(String s){
            Log.d(TAG,"LoadWebBody : "+ s);
            if(!s.equals("Wrong")) {
                try {
                    final WeatherForFiveDay weatherForFiveDay = mGetXml.ExtractValueFromJsonFiveDay(s);
                    Log.d(TAG, "LoadWebBody : " + weatherForFiveDay.getFiveDayDataSets().size());
                    updateDisplayOpen(weatherForFiveDay.getCountry() + "/" +
                            weatherForFiveDay.getLocation(), weatherForFiveDay.getFiveDayDataSets().get(0));
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
            String json = mGetXml.getXmlFromUrl(param);
            Log.d(TAG," LoadWebBody : Do ConditionAddress");
            Log.d(TAG," LoadWebBody : json = "+json);
            String address = mGetXml.extractLongLatFromAddress(json);
            Log.d(TAG," LoadWebBody : address = "+address);
            return  String.format(mGetXml.FORECAST,address);
        }
        return " wrong ";
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
                updateDisplayOpen(weatherForFiveDay.getCountry()+
                        "/"+weatherForFiveDay.getLocation(),dataSets.get(entry.getXIndex()));
            }
            @Override
            public void onNothingSelected() { }
        });

    }

}
