package com.maws.loonandroid.models;

import java.util.Date;

/**
 * Created by Andrexxjc on 10/05/2015.
 * This object represents an alert fired by the system.
 */
public class Alert {

    private long id;
    private long sensorId;
    private int sensorServiceId;
    private Date alertDate;
    private boolean isOn = true;
    private boolean dismissed = false;
    private Date dismissedDate;
    private long totalTimeAlarm;
    private long costumerId;
    private long siteId;

    public long getId() { return id; }
    public void setId(long id) { this.id = id;}
    public boolean isDismissed() { return dismissed; }
    public void setDismissed(boolean dismissed) { this.dismissed = dismissed; }
    public int getSensorServiceId() { return sensorServiceId; }
    public void setSensorServiceId(int sensorServiceId) { this.sensorServiceId = sensorServiceId; }
    public Date getAlertDate() { return alertDate; }
    public void setAlertDate(Date alertDate) { this.alertDate = alertDate; }
    public long getSensorId() {return sensorId;}
    public void setSensorId(long sensorId) {this.sensorId = sensorId;}
    public boolean isOn() {return isOn;}
    public void setIsOn(boolean isOn) {this.isOn = isOn;}
    public Date getDismissedDate() { return dismissedDate; }
    public void setDismissedDate(Date dismissedDate) { this.dismissedDate = dismissedDate; }
    public long getTotalTimeAlarm() { return totalTimeAlarm; }
    public void setTotalTimeAlarm(long totalTimeAlarm) { this.totalTimeAlarm = totalTimeAlarm; }
    public long getCostumerId() { return costumerId; }
    public void setCostumerId(long costumerId) { this.costumerId = costumerId; }
    public long getSiteId() { return siteId; }
    public void setSiteId(long siteId) { this.siteId = siteId; }
}