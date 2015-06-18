package com.maws.loonandroid.models;

import android.app.Service;

import com.maws.loonandroid.R;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Andrexxjc on 05/06/2015.
 */
public class DeviceService {

    //let's map the UUIDs of the services here
    public static final UUID UUID_BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_DEVICE_INFORMATION = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_THERMOMETER_SERVICE = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CARE_SENTINEL_SERVICE = UUID.fromString("79f7744a-f8e6-4810-8f16-140b6974835d");

    public static final HashMap<Integer, Integer> serviceNames = new HashMap<Integer, Integer>();

    public static final int SENSOR_BED = 3; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int SENSOR_CHAIR = 2; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int SENSOR_TOILET = 0; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int SENSOR_INCONTINENCE = 1; //these indexes are mapped to the sensor bit on the device characteristic value.

    static{
        serviceNames.put(SENSOR_BED, R.string.sensor_bed);
        serviceNames.put(SENSOR_CHAIR, R.string.sensor_chair);
        serviceNames.put(SENSOR_TOILET, R.string.sensor_toilet);
        serviceNames.put(SENSOR_INCONTINENCE, R.string.sensor_incontinence);
    }

    public enum ServiceType{
        Battery, DeviceInformation, Thermometer, CareSentinel, None
    }

    public static ServiceType getServiceType(final UUID uuid){
        if(uuid.equals(UUID_BATTERY_SERVICE)){
                return ServiceType.Battery;
        }
        else if(uuid.equals(UUID_DEVICE_INFORMATION)){
            return ServiceType.DeviceInformation;
        }
        else if(uuid.equals(UUID_THERMOMETER_SERVICE)){
            return ServiceType.Thermometer;
        }
        else if(uuid.equals(UUID_CARE_SENTINEL_SERVICE)){
            return ServiceType.CareSentinel;
        }
        return ServiceType.None;
    }

}
