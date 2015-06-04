package com.maws.loonandroid.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Andrexxjc on 10/05/2015.
 * This object holds the configuration of each one of the services inside the sensor
 */
public class SensorService {

    //let's map the UUIDs of the services here
    private static final UUID UUID_BATTERY_SERVICE =
            UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_DEVICE_INFORMATION =
            UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_THERMOMETER_SERVICE =
            UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_GENERIC_ACCESS =
            UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_SOMETHING =
            UUID.fromString("79f7744a-f8e6-4810-8f16-140b6974835d");

    private static final int ALARM_SOUND_1 = 0;
    private static final int ALARM_SOUND_2 = 1;
    private static final int ALARM_SOUND_3 = 2;
    private static final int ALARM_SOUND_4 = 3;
    private static final int ALARM_SOUND_5 = 4;

    private long id;
    private String name;
    private int alarm = ALARM_SOUND_1;
    private boolean on = true;
    private long sensorId;

    public SensorService(){}

    public SensorService(String name){
        this.name = name;
    }

    public long getId() { return id;}

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getAlarm() { return alarm; }

    public void setAlarm(int alarm) { this.alarm = alarm; }

    public boolean isOn() { return on; }

    public void setOn(boolean on) { this.on = on; }

    public long getSensorId() { return sensorId; }

    public void setSensorId(long sensorId) { this.sensorId = sensorId; }

    public static List<SensorService> createDefaultSensorServices(){

        List<SensorService> toReturn = new ArrayList<SensorService>();
        toReturn.add(new SensorService("Bed Sensor"));
        toReturn.add(new SensorService("Chair Sensor"));
        toReturn.add(new SensorService("Toilet Sensor"));
        return toReturn;
    }


}
