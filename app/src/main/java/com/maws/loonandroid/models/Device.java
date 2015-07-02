package com.maws.loonandroid.models;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Andrexxjc on 12/04/2015.
 * modified by Andres.Prada on 6/17/2015
 */
public class Device implements Parcelable {

    private long id = -1;
    private String name;
    private String hardwareId;
    private Date createdAt;
    private Date updatedAt;
    private String code;
    private String firmwareVersion;
    private String hardwareVersion;
    private String description;
    private String macAddress;
    private boolean connected = false;
    private boolean active = true;
    private int batteryStatus;
    private int signalStrength;
    private double temperature;

    public Device(){}

    public Device(BluetoothDevice device){
        this.name = device.getName();
        this.macAddress = device.getAddress();
    }

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

    public int getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(int batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrenght) {
        this.signalStrength = signalStrenght;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getHardwareId() { return hardwareId; }

    public void setHardwareId(String hardwareId) { this.hardwareId = hardwareId; }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
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
        pc.writeString(this.hardwareId);
        pc.writeString(this.firmwareVersion);
        pc.writeString(this.hardwareVersion);
        pc.writeString(this.description);
        pc.writeString(this.macAddress);
        pc.writeInt(this.batteryStatus);
        pc.writeInt(this.signalStrength);
        pc.writeDouble(this.temperature);
        pc.writeInt(this.active ? 1 : 0);
        pc.writeInt(this.connected ? 1: 0);
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        public Device createFromParcel(Parcel pc) {
            return new Device(pc);
        }
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    /**
     * Creator from Parcel, reads back fields in the same order they were written
     * */
    public Device(Parcel pc){

        this.id = pc.readLong();
        this.name = pc.readString();
        this.code = pc.readString();
        this.hardwareId = pc.readString();
        this.firmwareVersion = pc.readString();
        this.hardwareVersion = pc.readString();
        this.description = pc.readString();
        this.macAddress = pc.readString();
        this.batteryStatus = pc.readInt();
        this.signalStrength = pc.readInt();
        this.temperature = pc.readDouble();
        this.active = pc.readInt() == 1;
        this.connected = pc.readInt() == 1;
    }

    public static Device createFakeDevice(){

        long currentTime = (new Date()).getTime();
        String currentTimeStr = String.valueOf(currentTime);
        Device device = new Device();
        device.setName("Device " + currentTimeStr.substring(currentTimeStr.length() - 4));
        device.setCode(currentTimeStr.substring(currentTimeStr.length() - 4));
        device.setHardwareId(currentTimeStr);
        device.setFirmwareVersion("1.0");
        device.setHardwareVersion("1.1");
        device.setDescription("");
        device.setMacAddress("00-00-" + currentTimeStr.substring(currentTimeStr.length() - 4, currentTimeStr.length() - 2 ) + "-"+ currentTimeStr.substring(currentTimeStr.length() - 2));
        device.setBatteryStatus(3);
        device.setSignalStrength(5);
        device.setTemperature(80f);
        device.setActive(true);
        device.setConnected(false);
        return device;
    }

    public float getCelsiusTemperature(){
        return 0f;
    }

}
