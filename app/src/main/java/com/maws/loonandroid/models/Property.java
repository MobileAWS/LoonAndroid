package com.maws.loonandroid.models;

/**
 * Created by Andres on 6/18/2015.
 */
public class Property {

    private String id;
    private String value;

    public Property(){
        super();
    }
    public Property(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }
}
