package com.dat.stormy.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dat.stormy.model.DrawChart;
import com.dat.stormy.model.FiveDayDataSet;
import com.dat.stormy.R;
import com.dat.stormy.model.JsonHtmlService;
import com.dat.stormy.model.Locate;
import com.dat.stormy.model.SaveLocation;
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


public class MainActivity extends ActionBarActivity {

    final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.timeLabel)
    TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel)
    TextView mTemperatureValue;
    @InjectView(R.id.humidityValue)
    TextView mHumidityLabel;
    @InjectView(R.id.precipValue)
    TextView mPercipValue;
    @InjectView(R.id.summaryLabel)
    TextView mSummaryLabel;
    @InjectView(R.id.iconImageView)
    ImageView mIconImageView;
    @InjectView(R.id.locationLabel)
    TextView mLocationlabel;
    @InjectView(R.id.Chart)
    LineChart mChart;
    @InjectView(R.id.MainActivityBackgroundLinear)
    LinearLayout mLinearLayout;

    JsonHtmlService mJsonHtmlService = new JsonHtmlService(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        Intent intent = getIntent();
        WeatherForFiveDay weatherForFiveDay = (WeatherForFiveDay) intent.getExtras().getSerializable("FORECAST");
        updateDisplayOpen(weatherForFiveDay.getCountry() + "/" +
                weatherForFiveDay.getLocation(), weatherForFiveDay.getFiveDayDataSets().get(0));
        CreateChart(weatherForFiveDay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Set as default location");
                alertDialog.setMessage("Are you sure you want to set this location as default for widget");
                alertDialog.setIcon(R.drawable.ic_launcher);
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveDefaultLocation();
                    }
                });
                alertDialog.show();
                return true;
			case R.id.action_back:
				Intent intent = new Intent(this,location.class);
				startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDisplayOpen(String location, FiveDayDataSet weatherOpen) {
        if (weatherOpen.getTemperature() < 28) {
            mLinearLayout.setBackgroundColor(Color.rgb(40, 122, 171));
        } else mLinearLayout.setBackgroundColor(Color.rgb(255, 127, 0));

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

    private void saveDefaultLocation() {
        Intent intent = getIntent();
        Locate locate = (Locate) intent.getExtras().getSerializable("LOCATION_INFO");

        SaveLocation saveLocation = new SaveLocation(MainActivity.this);
        saveLocation.saveLocation(locate);
    }

}
