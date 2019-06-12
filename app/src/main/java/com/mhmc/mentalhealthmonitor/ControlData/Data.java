package com.mhmc.mentalhealthmonitor.ControlData;

import android.graphics.Bitmap;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;


public class Data {
    private String time, content, icon_type;
    private ArrayList<BarEntry> yVals, yValsSystem;
    private ArrayList<Bitmap> chartList, chartListSystem;//emotion

    public Data() {
    }

    public Data(String content, String icon_type, String time, ArrayList<BarEntry> yVals, ArrayList<Bitmap> chartList) {
        this.content = content.toString();
        this.icon_type = icon_type;
        this.time = time;
        this.yVals = yVals;
        this.chartList = chartList;
        this.yValsSystem = null;
        this.chartListSystem = null;
    }

    public Data(String content, String icon_type, String time, ArrayList<BarEntry> yVals, ArrayList<Bitmap> chartList, ArrayList<BarEntry> yValsSystem, ArrayList<Bitmap> chartListSystem) {
        this.content = content.toString();
        this.icon_type = icon_type;
        this.time = time;
        this.yVals = yVals;
        this.chartList = chartList;
        this.yValsSystem = yValsSystem;
        this.chartListSystem = chartListSystem;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getIcon_type() {
        return icon_type;
    }

    public ArrayList<BarEntry> getyVals() {
        return yVals;
    }

    public ArrayList<Bitmap> getchartList() {
        return chartList;
    }

    public ArrayList<BarEntry> getyValsSystem() {
        return yValsSystem;
    }

    public ArrayList<Bitmap> getchartListSystem() {
        return chartListSystem;
    }
}
