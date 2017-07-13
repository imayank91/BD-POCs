package com.RMC.BDCloud.Android;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Mayank Saini on 2/15/2016.
 */
public class BDPreferences {
    Context context;
    SharedPreferences prefs;

    public BDPreferences(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0);
    }

    public float getTransientLatitude() {
        return prefs.getFloat("latitude", 0);
    }

    public void setTransientLatitude(float value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("latitude", value);
        editor.apply();
        editor.commit();
    }

    public float getTransientLongitude() {
        return prefs.getFloat("longitude", 0);
    }

    public void setTransientLongitude(float value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("longitude", value);
        editor.apply();
        editor.commit();
    }

    public float getTransientAccuracy() {
        return prefs.getFloat("accuracy", 0);
    }

    public void setTransientAccuracy(float value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("accuracy", value);
        editor.apply();
        editor.commit();
    }

    public float getTransientAltitude() {
        return prefs.getFloat("altitude", 0);
    }

    public void setTransientAltitude(float value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("altitude", value);
        editor.apply();
        editor.commit();
    }

    public String getFirebaseToken() {
        return prefs.getString("firebaseToken", "");
    }

    public void setFirebaseToken(String value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("firebaseToken", value);
        editor.apply();
        editor.commit();
    }

    public boolean isFirebaseRegistered() {
        return prefs.getBoolean("firebaseRegistered", false);
    }

    public void firebaseRegistered(boolean value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firebaseRegistered", value);
        editor.apply();
        editor.commit();
    }

    public void setBeaconsLastSeen(long value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("beaconsLastSeen", value);
        editor.apply();
        editor.commit();

    }

    public long getBeaconsLastSeen() {
        return prefs.getLong("beaconsLastSeen", 0);
    }

    public void setOrganisationId(String orgId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("organisationId", orgId);
        editor.apply();
        editor.commit();
    }

    public String getOrganisationId() {
        return prefs.getString("organisationId", "");
    }

    public void setBeaconsLastCheckin(long value) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("beaconsLastCheckin", value);
        editor.apply();
        editor.commit();

    }

    public long getBeaconsLastCheckin() {
        return prefs.getLong("beaconsLastCheckin", 0);
    }

}
