package com.RareMediaCompany.BDPro.Helpers;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Mayank Saini on 2/15/2016.
 */
public class PreferenceforApp {
    Context context;
    SharedPreferences prefs;

    public PreferenceforApp(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(Constant.PREFS_NAME, 0);
    }

    public boolean getUserActivated() {
        return prefs.getBoolean("userActivated", false);
    }

    public void setUserActivated(boolean flag) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("userActivated", flag);
        editor.commit();
    }

    public String getUserName() {
        return prefs.getString("userName", "");
    }

    public void setUserName(String name) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userName", name);
        editor.commit();
    }

    public String getUserId() {
        return prefs.getString("userID", "");
    }

    public void setUserId(String name) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userID", name);
        editor.commit();
    }

    public String getPassword() {
        return prefs.getString("password", "");
    }

    public void setPassword(String pass) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("password", pass);
        editor.commit();
    }

    public float getTransientLatitude() {
        return prefs.getFloat("latitude", 0);
    }

    public void setTransientLatitude(float value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("latitude", value);
        editor.commit();
    }

    public float getTransientLongitude() {
        return prefs.getFloat("longitude", 0);
    }

    public void setTransientLongitude(float value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("longitude", value);
        editor.commit();
    }

    public float getTransientAccuracy() {
        return prefs.getFloat("accuracy", 0);
    }

    public void setTransientAccuracy(float value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("accuracy", value);
        editor.commit();
    }

    public float getTransientAltitude() {
        return prefs.getFloat("altitude", 0);
    }

    public void setTransientAltitude(float value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("altitude", value);
        editor.commit();
    }


}

