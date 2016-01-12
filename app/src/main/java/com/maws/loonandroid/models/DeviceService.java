package com.maws.loonandroid.models;

import android.app.Service;

import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.DevicePropertyDao;

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
    public static final UUID UUID_CARECOM_SERVICE = UUID.fromString("4ce30f4c-b14c-49e6-b001-1c83488a9964");
    public static final UUID UUID_SERVICE_GENERIC_ACCESS = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");

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
