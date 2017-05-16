package com.RMC.BDCloud.RealmDB.Model;

import com.RMC.BDCloud.Android.Constants;

import org.json.JSONObject;

import java.util.Date;

import io.realm.RealmCollection;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mayanksaini on 26/10/16.
 */

public class RMCCheckin extends RealmObject {

    public String latitude;
    public String longitude;
    public String accuracy;
    public String altitude;
    public String organizationId;
    @PrimaryKey
    public String checkinId;
    public String checkinDetails;
    public Date time;
    public String checkinCategory;
    public String checkinType;
    public String assignmentId;
    public String imageUrl;
    public RealmList<RMCImageUris> imageUri;
    public RealmList<RMCImageNames> imageName;
    public String aIds;
    public RealmList<RMCBeacon> rmcBeacons;


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

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    public String getCheckinDetails() {
        return checkinDetails;
    }

    public void setCheckinDetails(String checkinDetails) {
        this.checkinDetails = checkinDetails;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCheckinCategory() {
        return checkinCategory;
    }

    public void setCheckinCategory(String checkinCategory) {
        this.checkinCategory = checkinCategory;
    }

    public String getCheckinType() {
        return checkinType;
    }

    public void setCheckinType(String checkinType) {
        this.checkinType = checkinType;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

//    public String getImageUri() {
//        return imageUri;
//    }
//
//    public void setImageUri(String imageUri) {
//        this.imageUri = imageUri;
//    }

//    public String getImageName() {
//        return imageName;
//    }
//
//    public void setImageName(String imageName) {
//        this.imageName = imageName;
//    }

    public RealmList<RMCBeacon> getRmcBeacons() {
        return rmcBeacons;
    }

    public void setRmcBeacons(RealmList<RMCBeacon> rmcBeacons) {
        this.rmcBeacons = rmcBeacons;
    }

    public String getaIds() {
        return aIds;
    }

    public void setaIds(String aIds) {
        this.aIds = aIds;
    }

    public void setImageUri(RealmList<RMCImageUris> imageUri) {
        this.imageUri = imageUri;
    }

    public RealmList<RMCImageUris> getImageUri() {
        return imageUri;
    }

    public RealmList<RMCImageNames> getImageName() {
        return imageName;
    }

    public void setImageName(RealmList<RMCImageNames> imageName) {
        this.imageName = imageName;
    }
}

