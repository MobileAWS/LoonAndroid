package com.maws.loonandroid.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorServiceDao;

import java.util.Date;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class Sensor implements Parcelable {

    private long id;
    private String name;
    private String code;
    private String serial;
    private String version;
    private String description;
    private String macAddress;
    private boolean active = true;
    private float batteryStatus;
    private int signalStrength;
    private String temperature;
    private List<SensorService> sensorServices;

    public Sensor(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public float getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(float batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrenght) {
        this.signalStrength = signalStrenght;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() { return description;}

    public void setDescription(String description) { this.description = description; }

    public String getMacAddress() { return macAddress; }

    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public long getId() { return id;}

    public void setId(long id) { this.id = id;}

    public boolean isActive() { return active;}

    public void setActive(boolean active) { this.active = active; }

    public List<SensorService> getSensorServices() {
        return sensorServices;
    }

    public void setSensorServices(List<SensorService> sensorServices) {
        this.sensorServices = sensorServices;
    }

    //methods to handle the parcelable implementation
    @Override
    public int describeContents() {
        // ignore for now
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeLong(this.id);
        pc.writeString(this.name);
        pc.writeString(this.code);
        pc.writeString(this.serial);
        pc.writeString(this.version);
        pc.writeString(this.description);
        pc.writeString(this.macAddress);
        pc.writeFloat(this.batteryStatus);
        pc.writeInt(this.signalStrength);
        pc.writeString(this.temperature);
        pc.writeInt(this.active ? 1: 0);
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Sensor> CREATOR = new Parcelable.Creator<Sensor>() {
        public Sensor createFromParcel(Parcel pc) {
            return new Sensor(pc);
        }
        public Sensor[] newArray(int size) {
            return new Sensor[size];
        }
    };

    /**
     * Creator from Parcel, reads back fields in the same order they were written
     * */
    public Sensor(Parcel pc){

        this.id = pc.readLong();
        this.name = pc.readString();
        this.code = pc.readString();
        this.serial = pc.readString();
        this.version = pc.readString();
        this.description = pc.readString();
        this.macAddress = pc.readString();
        this.batteryStatus = pc.readFloat();
        this.signalStrength = pc.readInt();
        this.temperature = pc.readString();
        this.active = pc.readInt() == 1;
    }

    public static Sensor createFakeSensor(){

        long currentTime = (new Date()).getTime();
        String currentTimeStr = String.valueOf(currentTime);
        Sensor sensor = new Sensor();
        sensor.setName("Sensor " + currentTimeStr.substring(currentTimeStr.length() - 4));
        sensor.setCode(currentTimeStr.substring(currentTimeStr.length() - 4));
        sensor.setSerial(currentTimeStr);
        sensor.setVersion("v1");
        sensor.setDescription("");
        sensor.setMacAddress("00-00-00-00");
        sensor.setBatteryStatus(3);
        sensor.setSignalStrength(5);
        sensor.setTemperature("80");
        sensor.setActive(true);
        sensor.setSensorServices( SensorService.createFakeSensorServices() );
        return sensor;
    }

    public void loadServices(Context context){

        LoonMedicalDao lDao = new LoonMedicalDao(context);
        SensorServiceDao sDao = new SensorServiceDao(context);
        this.sensorServices = sDao.getAllBySensorId(this.id, lDao.getReadableDatabase());
    }
}
