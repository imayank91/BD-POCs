package com.RMC.BDCloud.RealmDB.Model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mayanksaini on 27/04/17.
 */

public class RMCPlace extends RealmObject {

    @PrimaryKey
    public String placeId;
    public Date addedOn;
    public Date updatedOn;
    public String placeAddress;
    public RMCLocation location;
    public String placeDetails;
    public RealmList<Users> usersList;


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Date addedOn) {
        this.addedOn = addedOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public RMCLocation getLocation() {
        return location;
    }

    public void setLocation(RMCLocation location) {
        this.location = location;
    }

    public String getPlaceDetails() {
        return placeDetails;
    }

    public void setPlaceDetails(String placeDetails) {
        this.placeDetails = placeDetails;
    }

    public RealmList<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(RealmList<Users> usersList) {
        this.usersList = usersList;
    }
}
