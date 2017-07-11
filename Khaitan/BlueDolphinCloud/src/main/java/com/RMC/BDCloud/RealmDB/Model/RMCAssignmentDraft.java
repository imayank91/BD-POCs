package com.RMC.BDCloud.RealmDB.Model;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by niks on 16/3/17.
 */

public class RMCAssignmentDraft extends RealmObject implements Serializable {

    @PrimaryKey
    public String assignmentId;
    public String address;
    public Date assignmentDeadline;
    public Date assignmentStartTime;
    public RMCLocation location;
    public String contactNumber;
    public String contactPerson;
    public String email;
    public String assignmentDetails;
    public String jobNumber;
    public String draft_description;


    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getAssignmentDeadline() {
        return assignmentDeadline;
    }

    public void setAssignmentDeadline(Date assignmentDeadline) {
        this.assignmentDeadline = assignmentDeadline;
    }

    public Date getAssignmentStartTime() {
        return assignmentStartTime;
    }

    public void setAssignmentStartTime(Date assignmentStartTime) {
        this.assignmentStartTime = assignmentStartTime;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public RMCLocation getLocation() {
        return location;
    }

    public void setLocation(RMCLocation location) {
        this.location = location;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAssignmentDetails() {
        return assignmentDetails;
    }

    public void setAssignmentDetails(String assignmentDetails) {
        this.assignmentDetails = assignmentDetails;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getDraft_description() {
        return draft_description;
    }

    public void setDraft_description(String draft_description) {
        this.draft_description = draft_description;
    }

}
