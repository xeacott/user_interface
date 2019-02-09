package com.example.user_interface;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class Analytics {

    // Unique instance of class
    private static Analytics instance = new Analytics();

    // Returns this instance
    public static Analytics getInstance() {
        return instance;
    }

    // Set this instance
    public static void setInstance(Analytics instance) {
        Analytics.instance = instance;
    }

    // Create data type enumerations
    public enum dataType {
        HR, EKG, POA, POB, POx;

        public static dataType fromInteger(int pos) {
            switch(pos) {
                case 0:
                    return HR;
                case 1:
                    return EKG;
                case 2:
                    return POA;
                case 3:
                    return POB;
                case 4:
                    return POx;
            }
            return null;
        }
    }

    // Create library of tags
    private static final String[] TagDef = { "<hr>", "<ekg>", "<POA>", "<POB>", "<POx>"};
    private static final int maxMemberLength = 2500;

    // Graph variables
    private String bluetooth_event;
    public LineGraphSeries<DataPoint> hrSeries = new LineGraphSeries<>();
    public LineGraphSeries<DataPoint> ekgSeries = new LineGraphSeries<>();
    public LineGraphSeries<DataPoint> pxSeries = new LineGraphSeries<>();

    // Data containers that are ArrayLists of Doubles
    ArrayList<Double> HR = new ArrayList<>();
    ArrayList<Double> EKG = new ArrayList<>();
    ArrayList<Double> POA = new ArrayList<>();
    ArrayList<Double> POB = new ArrayList<>();
    ArrayList<Double> POx = new ArrayList<>();

    // Default Constructor
    private Analytics() {
    }

    public String getBluetooth_event() {
        return bluetooth_event;
    }

    // Contains current packet of information from device
    public void setBluetooth_event(String bluetooth_event) {
        this.bluetooth_event = bluetooth_event;
    }


    public void startDecode() {
        dataDecode(getBluetooth_event());
    }

    public void dataDecode(String msg) {
        String header = msg.substring(3, 4);
        String temp;
        double dataValue;
        byte[] sensorDataStatus = new byte[8];

        for (int i = 0; i < 8; i++) {
//            sensorDataStatus[i] = (header >> i) & 1;
        }

        // Strip all the newline backslashes to fix string
        msg = msg.replace("\\", "");

        // Iterate through tag definitions to find the value it corresponds with
        for (int i = 0; i < 5; i++) {
            if(sensorDataStatus[i] == 1) {
                temp = msg.split(TagDef[i])[1];
                dataValue = Double.valueOf(temp);
                StorageUpdate(dataType.fromInteger(i), dataValue);
            }
        }
    }

    public void StorageUpdate(dataType type, double DataValue) {
        switch (type) {
            case HR:
                if (HR.size() > maxMemberLength) {
                    HR.remove(0);
                }
                HR.add(DataValue);
                break;
            case EKG:
                if (EKG.size() > maxMemberLength) {
                    EKG.remove(0);
                }
                EKG.add(DataValue);
                break;
            case POA:
                if (POA.size() > maxMemberLength) {
                    POA.remove(0);
                }
                POA.add(DataValue);
                break;
            case POB:
                if (POB.size() > maxMemberLength) {
                    POB.remove(0);
                }
                POB.add(DataValue);
                break;
            case POx:
                if (POx.size() > maxMemberLength) {
                    POx.remove(0);
                }
                POx.add(DataValue);
                break;
        }
    }
}