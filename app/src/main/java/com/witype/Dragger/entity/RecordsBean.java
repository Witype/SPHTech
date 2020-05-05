package com.witype.Dragger.entity;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordsBean implements CompareAble {

    /**
     * quarter : 2004-Q3
     * volume_of_mobile_data : 0.000384
     * _id : 1
     */

    private String quarterQStr;
    private String quarterYStr;
    private String quarter;
    private double volume_of_mobile_data;
    private double volume;
    private int _id;
    private double volume_offset;
    private double total_of_year;

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public double getVolume() {
        return volume <= 0 ? getVolume_of_mobile_data() : volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getVolume_of_mobile_data() {
        return volume_of_mobile_data;
    }

    public void setVolume_of_mobile_data(double volume_of_mobile_data) {
        this.volume_of_mobile_data = volume_of_mobile_data;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double getVolume_offset() {
        return volume_offset;
    }

    public void setVolume_offset(double volume_offset) {
        this.volume_offset = volume_offset;
    }

    public double getTotal_of_year() {
        return total_of_year <= 0 ? volume_of_mobile_data : total_of_year;
    }

    public void setTotal_of_year(double total_of_year) {
        this.total_of_year = total_of_year;
    }

    public String getQuarterQStr() {
        if (TextUtils.isEmpty(quarterQStr)) {
            Pattern pattern = Pattern.compile("Q[0-4]{1}");
            Matcher matcher = pattern.matcher(getQuarter());
            quarterQStr = matcher.find() ? matcher.group(0) : "";
        }
        return quarterQStr;
    }

    public String getQuarterYStr() {
        if (TextUtils.isEmpty(quarterYStr)) {
            Pattern pattern = Pattern.compile("[0-9]{4}");
            Matcher matcher = pattern.matcher(getQuarter());
            quarterYStr = matcher.find() ? matcher.group(0) : "";
        }
        return quarterYStr;
    }

    @Override
    public String getCompareKey() {
        return String.valueOf(get_id());
    }

    @Override
    public String toString() {
        return "RecordsBean{" +
                "quarter='" + quarter + '\'' +
                ", volume_of_mobile_data=" + volume_of_mobile_data +
                ", _id=" + _id +
                ", volume_offset=" + volume_offset +
                ", total_of_year=" + getTotal_of_year() +
                '}';
    }
}
