package com.RMC.BDCloud.Holders;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mayanksaini on 20/10/16.
 */

import com.RMC.BDCloud.Android.Constants.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class CheckinDataHolder {

    public String latitude;
    public String longitude;
    public String accuracy;
    public String altitude;
    public String organizationId;
    public String checkinId;
    public JsonObject checkinDetails;
    public String time;
    public String checkinCategory;
    public String checkinType;
    public String assignmentId;
    public String imageUrl;
    public JsonArray beaconProximities;
}
