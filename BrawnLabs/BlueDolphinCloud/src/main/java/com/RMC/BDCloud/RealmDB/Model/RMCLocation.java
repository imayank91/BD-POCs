package com.RMC.BDCloud.RealmDB.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mayanksaini on 18/11/16.
 */

public class RMCLocation extends RealmObject{

    public String type;
    public String altitude;
    public String accuracy;
    public String latitude;
    public String longitude;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
