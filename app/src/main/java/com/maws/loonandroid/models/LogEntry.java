package com.maws.loonandroid.models;

import com.maws.loonandroid.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andrexxjc on 14/09/2015.
 */
public class LogEntry {

    private long id;
    private String message;
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return Util.logDateFormat.format(this.date) + " - " + this.message;
    }
}
