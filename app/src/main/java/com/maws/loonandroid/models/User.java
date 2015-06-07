package com.maws.loonandroid.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Andrexxjc on 14/04/2015.
 */
public class User {

    private long id;
    private String name;
    private String email;
    private String password;

    public User(){}
    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    /*This will save the user in the preferences so we have it available for all the app*/
    private static final String USER_PREFS = "Usr_prfs";
    private static final String USER_PREFS_NAME = "Usr_n";
    private static final String USER_PREFS_EMAIL = "Usr_e";
    private static final String USER_PREFS_PASSWORD = "Usr_p";
    private static final String USER_PREFS_ID = "Usr_i";

    public static User instance = null;
    public static void setCurrent(User user, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE).edit();
        editor.putLong(USER_PREFS_ID, user.getId());
        editor.putString(USER_PREFS_NAME, user.getName());
        editor.putString(USER_PREFS_EMAIL, user.getEmail());
        editor.putString(USER_PREFS_PASSWORD, user.getPassword());
        editor.apply();
        instance = user;
    }

    public static User getCurrent(Context context){
        if(instance == null){
            SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
            instance = new User();
            instance.setName(prefs.getString(USER_PREFS_NAME, ""));
            instance.setEmail(prefs.getString(USER_PREFS_EMAIL, ""));
            instance.setPassword(prefs.getString(USER_PREFS_PASSWORD, ""));
        }
        return instance;
    }

}
