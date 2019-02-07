package com.example.user_interface;

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
    enum dataType {
        HR, EKG, POA, POB, POx
    }

    // library of tags
    public static String[] startTagDef = {"<hr>", "<ekg>", "<POA>", "<POB>", "<POx>"};
    public static String[] endTagDef = {"<\\hr>", "<\\ekg>", "<\\POA>", "<\\POB>", "<\\POx>"};

    public static final int maxMemberLength = 2500;

    // Data containers that are ArrayLists of Doubles
    ArrayList<Double> HR = new ArrayList<>();
    ArrayList<Double> EKG = new ArrayList<>();
    ArrayList<Double> POA = new ArrayList<>();
    ArrayList<Double> POB = new ArrayList<>();
    ArrayList<Double> POx = new ArrayList<>();

    private String bluetooth_event;

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

    }


    public void dataDecode(String msg) {
        String header = msg.substring(3, 4);
        String temp = null;
        byte[] sensorDataStatus = new byte[8];

        for (int i = 0; i < 8; i++) {
            sensorDataStatus[i] = (header >> i) & 1;
        }

        for (int i = 0; i < 5; i++) {
            if(sensorDataStatus[i] == 1) {
                temp = msg.split(startTagDef[i][1])
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