package com.maws.loonandroid.models;

/**
 * Created by Andrexxjc on 14/04/2015.
 */
public class User {

    private String name;
    private String email;
    private String password;
    private String siteId;

    public User(){}
    public User(String name, String email, String password, String siteId){
        this.name = name;
        this.email = email;
        this.password = password;
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getSiteId() { return siteId; }

    public void setSiteId(String siteId) { this.siteId = siteId; }

}
