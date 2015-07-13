package com.dat.stormy.view;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.dat.stormy.model.DrawChart;
import com.dat.stormy.model.FiveDayDataSet;
import com.dat.stormy.R;
import com.dat.stormy.model.GetXml;
import com.dat.stormy.model.WeatherForFiveDay;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity {

    final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.timeLabel)     TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel)     TextView mTemperatureValue;
    @InjectView(R.id.humidityValue)     TextView mHumidityLabel;
    @InjectView(R.id.precipValue)     TextView mPercipValue;
    @InjectView(R.id.summaryLabel)     TextView mSummaryLabel;
    @InjectView(R.id.iconImageView)     ImageView mIconImageView;
    @InjectView(R.id.locationLabel)     TextView mLocationlabel;
    @InjectView(R.id.Chart)     LineChart mChart;

    GetXml mGetXml = new GetXml(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        Intent intent = getIntent();
        WeatherForFiveDay weatherForFiveDay =  (WeatherForFiveDay) intent.getExtras().getSerializable("FORECAST");
        updateDisplayOpen(weatherForFiveDay.getCountry() + "/" +
                weatherForFiveDay.getLocation(), weatherForFiveDay.getFiveDayDataSets().get(0));
        CreateChart(weatherForFiveDay);
    }

    private void updateDisplayOpen(String location, FiveDayDataSet weatherOpen) {
        mTemperatureValue.setText(weatherOpen.getTemperature() + "");
        Date date = weatherOpen.getTime();
        String hour = new SimpleDateFormat("hh:mm a").format(date);
        mTimeLabel.setText("At " + date.getDate() + "/" + (date.getMonth() + 1)
                + " " + hour);
        mLocationlabel.setText(location);
        mHumidityLabel.setText((int) weatherOpen.getHumidity() + " %");
        double rain = weatherOpen.getRain() * 100;
        mPercipValue.setText(((float) Math.round(rain) / 100) + " mm");
        mSummaryLabel.setText(weatherOpen.getWeatherDescription());
        mIconImageView.setImageDrawable(getResources().getDrawable(weatherOpen.getWeatherIconId()));
    }

    private void CreateChart(final WeatherForFiveDay weatherForFiveDay) {
        final List<FiveDayDataSet> dataSets = weatherForFiveDay.getFiveDayDataSets();

        float[] temperatures = new float[5];
        String[] labelTemperatures = new String[5];

        for (int i = 0; i < dataSets.size(); i++) {
            FiveDayDataSet temp = dataSets.get(i);
            temperatures[i] = temp.getTemperature();
            labelTemperatures[i] = temp.getTime().getDate() + "/" + (temp.getTime().getMonth() + 1);
            Log.d(TAG, "DrawChart: " + temperatures[i] + " " + labelTemperatures[i]);
        }
        DrawChart drawChart = new DrawChart(mChart, MainActivity.this, temperatures, labelTemperatures);

        drawChart.StartDraw();

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                updateDisplayOpen(weatherForFiveDay.getCountry() +
                        "/" + weatherForFiveDay.getLocation(), dataSets.get(entry.getXIndex()));
            }

            @Override
            public void onNothingSelected() {
            }
        });

    }

}
