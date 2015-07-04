package com.dat.stormy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dat on 01/07/2015.
 */
public class DrawChart{
    private Context mContext;
    private LineChart mLineChart;

    private float[] mTemperatures;
    private String[] mLabelTemperatures;

    public DrawChart(LineChart lineChart, Context context, float[] temperatures, String[] labelTemperature){
        this.mLineChart = lineChart;
        this.mContext = context;
        this.mTemperatures = temperatures;
        this.mLabelTemperatures = labelTemperature;
    }

    public void StartDraw()
    {
        SetUp();
        addData();
    }

    private void addData(){
        float max = mTemperatures[0];
        float min = mTemperatures[0];
        List date = new ArrayList<String>();
        for(int i=0;i<mLabelTemperatures.length;i++)
        {
            date.add(mLabelTemperatures[i]);
        }

        List entries = new ArrayList<Entry>();
        for(int i=0;i<mTemperatures.length;i++){
            if(max<mTemperatures[i]) max = mTemperatures[i];
            if(min>mTemperatures[i]) min = mTemperatures[i];
            entries.add(new Entry(mTemperatures[i],i));
        }

        YAxis y1 = mLineChart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaxValue(max+5);
        y1.setAxisMinValue(min - 5);

        LineDataSet lineDataSet = new LineDataSet(entries,"Temperature");
        lineDataSet = setupLine(lineDataSet);

        LineData data = new LineData(date,lineDataSet);
        data.setValueTextColor(Color.WHITE);

        mLineChart.setData(data);

        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                LineData da = mLineChart.getLineData();

                Toast.makeText(mContext,"data : entry "+entry.toString()+" i "+i,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() { }
        });
    }

    private LineDataSet setupLine(LineDataSet lineDataSet){
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(ColorTemplate.getHoloBlue());
        lineDataSet.setCircleColor(ColorTemplate.getHoloBlue());
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleSize(4f);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setFillColor(ColorTemplate.getHoloBlue());
        lineDataSet.setHighLightColor(Color.rgb(244,117,177));
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setValueTextSize(10f);
        return lineDataSet;
    }

    private void SetUp(){

       // mLineChart.setDescription("Customize line chart Description");
        mLineChart.setNoDataTextDescription("No data for the moment");

        mLineChart.setHighlightEnabled(true);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setDrawGridBackground(false);

        mLineChart.setPinchZoom(true);
        mLineChart.setBackgroundColor(Color.LTGRAY);

        Legend legend = mLineChart.getLegend();

        //customize legend
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.WHITE);

        XAxis x1 = mLineChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);

        YAxis y1 = mLineChart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setDrawGridLines(true);
        y1.setStartAtZero(false);
        y1.setDrawLabels(false);
        y1.setDrawAxisLine(false);

        YAxis y12 = mLineChart.getAxisRight();
        y12.setEnabled(false);
    }




}