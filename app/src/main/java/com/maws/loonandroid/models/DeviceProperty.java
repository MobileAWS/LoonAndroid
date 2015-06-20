package com.maws.loonandroid.models;

/**
 * Created by Andres on 6/19/2015.
 */
public class DeviceProperty {

    private int id;
    private String value;

    public DeviceProperty() {
    }

    public DeviceProperty(String value, int id) {
        this.value = value;
        this.id = id;
    }

    public int getId() { return id;  }

    public void setId(int id) { this.id = id; }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }
}
