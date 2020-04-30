package com.witype.Dragger.entity;

public class RecordsBean implements CompareAble {

    /**
     * quarter : 2004-Q3
     * volume_of_mobile_data : 0.000384
     * _id : 1
     */

    private String quarter;
    private double volume_of_mobile_data;
    private int _id;
    private double volume_offset;

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
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
                '}';
    }
}
