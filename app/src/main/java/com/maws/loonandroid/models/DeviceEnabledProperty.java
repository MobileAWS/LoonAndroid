package com.maws.loonandroid.models;

/**
 * Created by Andrexxjc on 24/06/2015.
 */
public class DeviceEnabledProperty {

    long id;
    long propertyId;
    long deviceId;
    //TODO delete this part of model
    //long userId;
    boolean isEnabled = true;
    int delay = 0;

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
    //TODO delete this part of model
    /* public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }*/

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
