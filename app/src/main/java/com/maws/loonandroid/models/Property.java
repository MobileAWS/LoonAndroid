package com.maws.loonandroid.models;

import com.maws.loonandroid.R;

/**
 * Created by Andres on 6/18/2015.
 */
public class Property {

    //this will hold the default properties
    public static final Property[] defaultProperties;

    public static final int CODE_TOILET = 0; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int CODE_INCONTINENCE = 1; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int CODE_CHAIR = 2; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int CODE_BED = 3; //these indexes are mapped to the sensor bit on the device characteristic value.

    public static final String DEVICE_TOILET = "Toilet";
    public static final String DEVICE_INCONTINENCE = "Incontinence";
    public static final String DEVICE_CHAIR = "Chair";
    public static final String DEVICE_BED = "Bed";

    static{
        defaultProperties = new Property[4];
        defaultProperties[0] = new Property(CODE_TOILET, R.string.property_toilet, DEVICE_TOILET, "boolean" );
        defaultProperties[1] = new Property(CODE_INCONTINENCE, R.string.property_incontinence, DEVICE_INCONTINENCE, "boolean" );
        defaultProperties[2] = new Property(CODE_CHAIR, R.string.property_chair, DEVICE_CHAIR, "boolean" );
        defaultProperties[3] = new Property(CODE_BED, R.string.property_bed, DEVICE_BED, "boolean" );
    }

    public static Property getDefaultProperty(long id){
        for(int i = 0; i < defaultProperties.length; i++){
            if(defaultProperties[i].getId() == id){
                return defaultProperties[i];
            }
        }
        return null;
    }

    private long id;
    private String name;
    private String metric;
    private int displayId;

    public Property(){}

    public Property(long id, int displayId, String name, String metric) {
        this.id = id;
        this.displayId = displayId;
        this.name = name;
        this.metric = metric;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public int getDisplayId() {
        return displayId;
    }

    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }
}
