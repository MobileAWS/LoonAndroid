package com.maws.loonandroid.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Andrexxjc on 10/05/2015.
 * This object holds the configuration of each one of the services inside the sensor
 */
public class SensorCharacteristic {

    public static final String _CHAR_BATTERY_DATA = "2a19";
    public static final String _CHAR_THERMO_DATA = "2a1c";
    public static final UUID _CHAR_CARE_SENTINEL = UUID.fromString("64695F25-2326-430A-985F-AA4AE90DA42F");
    public static final String _CHAR_MODEL_NUMBER = "2a24";
    public static final String _CHAR_SERIAL_NUMBER = "2a25";
    public static final String _CHAR_MANUFACTURER = "2a29";
    public static final String _CHAR_FIRMWARE_REVISION = "2a26";
    public static final String _CHAR_HARDWARE_REVISION = "2a27";
    public static final UUID _DESCRIPTOR_CARE_SENTINEL_NOTIFICATIONS = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final int ALARM_SOUND_1 = 0;
    private static final int ALARM_SOUND_2 = 1;
    private static final int ALARM_SOUND_3 = 2;
    private static final int ALARM_SOUND_4 = 3;
    private static final int ALARM_SOUND_5 = 4;

    private long id;
    private String name;

    public SensorCharacteristic(){}

    public SensorCharacteristic(String name){
        this.name = name;
    }

    public long getId() { return id;}

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public static List<SensorCharacteristic> createDefaultSensorCharacteristics(){

        List<SensorCharacteristic> toReturn = new ArrayList<SensorCharacteristic>();
        toReturn.add(new SensorCharacteristic("Bed"));
        toReturn.add(new SensorCharacteristic("Chair"));
        toReturn.add(new SensorCharacteristic("Toilet"));
        toReturn.add(new SensorCharacteristic("Incontinence"));
        return toReturn;
    }


}
