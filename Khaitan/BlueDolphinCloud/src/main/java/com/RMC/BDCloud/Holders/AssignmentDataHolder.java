package com.RMC.BDCloud.Holders;

import com.RMC.BDCloud.Android.Constants.assignmentType;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mayanksaini on 24/10/16.
 */

public class AssignmentDataHolder {

    public String organizationId;
    public String assignmentId;
    public ArrayList<String> assigneeIds;
    public String addedOn;
    public String time;
    public String status;
    public String assignmentAddress;
    public String assignmentDeadline;
    public String assignmentStartTime;
    public String updatedOn;
    public String statusLog;
    public String latitude;
    public String longitude;
    public String altitude;
    public String accuracy;
    public String placeId;
    public ArrayList<String> imageUrls;
    public JsonObject assignmentDetails;

}
