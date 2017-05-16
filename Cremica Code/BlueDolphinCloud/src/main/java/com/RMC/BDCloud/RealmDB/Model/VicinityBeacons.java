package com.RMC.BDCloud.RealmDB.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mayanksaini on 23/01/17.
 */

public class VicinityBeacons extends RealmObject{

    @PrimaryKey
    public String beaconId;
    public String addedOn;
    public String updatedOn;
    public String uuid;
    public String major;
    public String minor;
    public String address;
    public String organizationId;
    public String placeId;
    public String beaconDetails;
    public RMCLocation location;



    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public RMCLocation getLocation() {
        return location;
    }

    public void setLocation(RMCLocation location) {
        this.location = location;
    }

    public String getBeaconDetails() {
        return beaconDetails;
    }

    public void setBeaconDetails(String beaconDetails) {
        this.beaconDetails = beaconDetails;
    }
}
