package com.maws.loonandroid.models;

import java.util.Date;

/**
 * Created by Andrexxjc on 10/05/2015.
 * This object represents an alert fired by the system.
 */
public class Alert {

    private long id;
    private long sensorServiceId;
    private Date alertDate;
    private boolean dismissed = false;

    public long getId() { return id; }

    public void setId(long id) { this.id = id;}

    public boolean isDismissed() { return dismissed; }

    public void setDismissed(boolean dismissed) { this.dismissed = dismissed; }

    public long getSensorServiceId() { return sensorServiceId; }

    public void setSensorServiceId(long sensorServiceId) { this.sensorServiceId = sensorServiceId; }

    public Date getAlertDate() { return alertDate; }

    public void setAlertDate(Date alertDate) { this.alertDate = alertDate; }
}
