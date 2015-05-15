package com.maws.loonandroid.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 10/05/2015.
 * This object holds the configuration of each one of the services inside the sensor
 */
public class SensorService {

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

    public static List<SensorService> createFakeSensorServices(){

        List<SensorService> toReturn = new ArrayList<SensorService>();
        toReturn.add(new SensorService("Bed Sensor"));
        toReturn.add(new SensorService("Chair Sensor"));
        toReturn.add(new SensorService("Toilet Sensor"));
        return toReturn;
    }
}
