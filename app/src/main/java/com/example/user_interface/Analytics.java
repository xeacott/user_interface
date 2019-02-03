package com.example.user_interface;


import com.jjoe64.graphview.series.DataPoint;

public class Analytics {

    private static Analytics instance = new Analytics();

    public static Analytics getInstance() {
        return instance;
    }

    public static void setInstance(Analytics instance) {
        Analytics.instance = instance;
    }

    private String bluetooth_event;
    private DataPoint single_data_point;

    private Analytics() {}

    public String getBluetooth_event() {
        return bluetooth_event;
    }

    public DataPoint getSingle_data_point() {
        return single_data_point;
    }

    public void setBluetooth_event(String bluetooth_event) {
        this.bluetooth_event = bluetooth_event;
    }

    public void setSingle_data_point(DataPoint value) {
        this.single_data_point = value;
    }

    public void convertString() {
        String[] test = new String[0];
        double x_value, y_value;

        test = getBluetooth_event().split(".");

        x_value = Double.parseDouble(test[0]);
        y_value = Double.parseDouble(test[1]);

        setSingle_data_point(x_value, y_value);
    }

}