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
    private RelativeLayout mRelativeLayout;
    private Context mContext;
    private LineChart mLineChart;

    private float[] mTemperatures;

    public DrawChart(RelativeLayout relativeLayout, Context context, float[] temperatures){
        this.mRelativeLayout = relativeLayout;
        this.mContext = context;
        this.mTemperatures = temperatures;
    }

    public void StartDraw()
    {
        SetUp();
        addData();
    }

    private void addData(){
        List date = new ArrayList<String>();
        date.add("Tue");
        date.add("wen");
        date.add("Thu");
        date.add("Fri");
        date.add("Sat");

        List entries = new ArrayList<Entry>();
        entries.add(new Entry(10,0));
        entries.add(new Entry(20,1));
        entries.add(new Entry(30,2));
        entries.add(new Entry(40,3));
        entries.add(new Entry(15,4));

        LineDataSet lineDataSet = new LineDataSet(entries,"# of call");
        lineDataSet = setupLine(lineDataSet);


        LineData data = new LineData(date,lineDataSet);
        data.setValueTextColor(Color.WHITE);

        mLineChart.setData(data);

        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
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

        mLineChart = new LineChart(mContext);
        mRelativeLayout.addView(mLineChart);

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
        y1.setAxisMaxValue(40f);
        y1.setAxisMinValue(-30f);
        y1.setDrawGridLines(true);
        y1.setStartAtZero(false);
        y1.setDrawLabels(false);
        y1.setDrawAxisLine(false);

        YAxis y12 = mLineChart.getAxisRight();
        y12.setEnabled(false);
    }




}