package com.maws.loonandroid.models;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class Sensor {

    private String name;
    private String code;
    private String serial;
    private String version;
    private float batteryStatus;
    private int signalStrenght;
    private String temperature;

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

    public int getSignalStrenght() {
        return signalStrenght;
    }

    public void setSignalStrenght(int signalStrenght) {
        this.signalStrenght = signalStrenght;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
