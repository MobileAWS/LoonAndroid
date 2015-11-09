package com.maws.loonandroid.models;

import java.util.Date;

/**
 * Created by Andres on 6/19/2015.
 */
public class DeviceProperty {

    private long id;
    private long deviceId;
    private long propertyId;
    private Date createdAt;
    private Date dismissedAt;
    private long totalTimeAlarm;
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDismissedAt() {
        return dismissedAt;
    }

    public void setDismissedAt(Date dismissedDate) {
        this.dismissedAt = dismissedDate;
    }

    public long getTotalTimeAlarm() {
        return totalTimeAlarm;
    }

    public void setTotalTimeAlarm(long totalTimeAlarm) {
        this.totalTimeAlarm = totalTimeAlarm;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
