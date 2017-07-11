package com.RMC.BDCloud.RealmDB.Model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mayanksaini on 18/11/16.
 */

public class RMCAssignment extends RealmObject {

    @PrimaryKey
    public String assignmentId;
    public Date addedOn;
    public Date time;
    public String status;
    public String address;
    public Date assignmentDeadline;
    public Date assignmentStartTime;
    public Date updatedOn;
    public String assignmentDetails;
    public RealmList<StatusLog> statusLog;
    public RealmList<RMCAssignee> assigneeData;
    public RMCAssigner assignerData;
    public RMCLocation location;
    public String assignmentType;
    public String localStatus;
    public String placeId;
    public boolean isOpened;
    public String imageUrl;

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }


    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Date addedOn) {
        this.addedOn = addedOn;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getAssignmentDetails() {
        return assignmentDetails;
    }

    public void setAssignmentDetails(String assignmentDetails) {
        this.assignmentDetails = assignmentDetails;
    }

    public RealmList<StatusLog> getStatusLog() {
        return statusLog;
    }

    public void setStatusLog(RealmList<StatusLog> statusLog) {
        this.statusLog = statusLog;
    }

    public RealmList<RMCAssignee> getAssigneeData() {
        return assigneeData;
    }

    public void setAssigneeData(RealmList<RMCAssignee> assigneeData) {
        this.assigneeData = assigneeData;
    }

    public RMCAssigner getAssignerData() {
        return assignerData;
    }

    public void setAssignerData(RMCAssigner assignerData) {
        this.assignerData = assignerData;
    }

    public RMCLocation getLocation() {
        return location;
    }

    public void setLocation(RMCLocation location) {
        this.location = location;
    }

    public Date getAssignmentDeadline() {
        return assignmentDeadline;
    }

    public void setAssignmentDeadline(Date assignmentDeadline) {
        this.assignmentDeadline = assignmentDeadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getAssignmentStartTime() {
        return assignmentStartTime;
    }

    public void setAssignmentStartTime(Date assignmentStartTime) {
        this.assignmentStartTime = assignmentStartTime;
    }

    public String getLocalStatus() {
        return localStatus;
    }

    public void setLocalStatus(String localStatus) {
        this.localStatus = localStatus;
    }


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}