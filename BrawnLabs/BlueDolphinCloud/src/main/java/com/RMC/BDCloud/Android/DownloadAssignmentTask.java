package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.RealmDB.Model.RMCAssignee;
import com.RMC.BDCloud.RealmDB.Model.RMCAssigner;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCLocation;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RMC.BDCloud.RealmDB.Model.StatusLog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.TimeZone;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.RMC.BDCloud.Android.BDCloudUtils.DEBUG_LOG;
import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by mayanksaini on 17/11/16.
 */

public class DownloadAssignmentTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private String assignmentId;
    private String notificationBody;
    String assigneeUserId;

    public DownloadAssignmentTask(Context context, RequestCallback requestCallback, String assignmentId, String notificationBody) {
        this.context = context;
        this.requestCallback = requestCallback;
        this.assignmentId = assignmentId;
        this.notificationBody = notificationBody;
    }

    public void downloadAssignment() {
        BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService
                (context, DownloadAssignmentTask.this);

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        String organizationId = user.getOrgId();
        String oauthToken = user.getToken();
        String userId = user.getUserId();
        realm.close();

        String url = Constants.BASE_URL + "organisation/" + organizationId + "/assignment?assignmentId=" + assignmentId;

        Log.i("DownloadAssignmentTask ", " " + url);

        JSONObject object = new JSONObject();
        try {
            object.put("authorization", oauthToken);
            object.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bdCloudPlatformService.executeHttpGet(url, context, Constants.downloadAssignmentsCallback, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mayBeDownloadAssignments(String status) {
        BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService
                (context, DownloadAssignmentTask.this);

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        String organizationId = user.getOrgId();
        String oauthToken = user.getToken();
        String userId = user.getUserId();
        realm.close();

        String url = Constants.BASE_URL + "organisation/" + organizationId +
                "/assignment?assigneeId=" + userId + "&status=" + status;

        Log.i("DownloadAssignmentTask ", " " + url);

        JSONObject object = new JSONObject();
        try {
            object.put("authorization", oauthToken);
            object.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bdCloudPlatformService.executeHttpGet(url, context, Constants.downloadAssignmentsCallback, object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateAssignmentStatus(Realm realm) {

        final RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        final BDPreferences prefs = new BDPreferences(context);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a checkin
                if (user != null) {
                    RMCCheckin rmcCheckin = new RMCCheckin();
                    rmcCheckin.setLatitude(String.valueOf(prefs.getTransientLatitude()));
                    rmcCheckin.setLongitude(String.valueOf(prefs.getTransientLongitude()));
                    rmcCheckin.setAccuracy(String.valueOf(prefs.getTransientAccuracy()));
                    rmcCheckin.setAltitude(String.valueOf(prefs.getTransientAltitude()));
                    rmcCheckin.setCheckinDetails(new JsonObject().toString());
                    rmcCheckin.setAssignmentId(assignmentId);
                    rmcCheckin.setCheckinCategory("Non-Transient");

                    rmcCheckin.setCheckinType("Downloaded");
                    String checkinId = UUID.randomUUID().toString();
                    if (DEBUG_LOG) {
                        Log.d("Update Assignment Status", "CheckinId-" + checkinId);
                        Log.d("Update Assignment Status", "AssignmentId-" + assignmentId);
                    }
                    rmcCheckin.setCheckinId(checkinId);
                    rmcCheckin.setOrganizationId(user.getOrgId());
                    rmcCheckin.setTime(new Date());
                    realm.copyToRealmOrUpdate(rmcCheckin);
                }
            }
        });

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {


        if (type == Constants.downloadAssignmentsCallback) {
            if (status == true) {

                Realm realm = BDCloudUtils.getRealmBDCloudInstance();
                try {
                    JSONObject data = object.getJSONObject("data");
                    JSONArray documents = data.getJSONArray("documents");

                    for (int i = 0; i < documents.length(); i++) {
                        JSONObject docs = documents.getJSONObject(i);
                        JSONObject assignmentData = docs.getJSONObject("assignmentData");
                        final String updatedOn = assignmentData.getString("updatedOn");
                        final String assignmentStatus = assignmentData.getString("status");
                        final String addedOn = assignmentData.getString("addedOn");
                        final String assignmentId = assignmentData.getString("assignmentId");
                        final String assignmentDeadline = assignmentData.getString("assignmentDeadline");
                        final String assignmentStarttime = assignmentData.getString("assignmentStartTime");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                        //format.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
//                        //format.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));/// STRING TO DATE
                        format.setTimeZone(TimeZone.getTimeZone("IST"));

                        //      Date assignStartDateToFormat = format.parse(assignmentStarttime);


                        final Date assignmentStartTimeInDate = format.parse(assignmentStarttime);
                        final Date assignmentDeadlineInDateFormat = format.parse(assignmentDeadline);
                        final Date addedOnInDateFormat = format.parse(addedOn);
                        final Date updatedOnInDateFormat = format.parse(updatedOn);

                        final String assignmentAddr = assignmentData.getString("assignmentAddress");
                        JSONObject location = assignmentData.getJSONObject("location");
                        String locType = location.getString("type");
                        String altitude = location.getString("altitude");
                        String accuracy = location.getString("accuracy");
                        JSONArray coordinates = location.getJSONArray("coordinates");
                        String latitude = coordinates.get(1).toString();
                        String longitude = coordinates.get(0).toString();
                        JSONObject associationIds = docs.getJSONObject("associationIds");
                        final JSONObject assignerData = associationIds.getJSONObject("assignerData");
                        final JSONArray assigneeDataArray = associationIds.getJSONArray("assigneeData");
                        JsonParser jsonParser = new JsonParser();
                        JsonObject assignmentDetails = (JsonObject) jsonParser.parse(docs.getJSONObject("assignmentDetails").toString());
                        String placeId = assignmentDetails.get("placeId").getAsString();
                        final String assignerUserId = assignerData.getString("userId");
                        String assignerOrgId = assignerData.getString("organizationId");
                        String assignerPrivelege = assignerData.getString("privelege");

                        final RealmList<RMCAssignee> assigneList = new RealmList<>();

                        for (int j = 0; j < assigneeDataArray.length(); j++) {

                            JSONObject assigneeData = assigneeDataArray.getJSONObject(j);
                            assigneeUserId = assigneeData.getString("userId");
                            String assigneeOrgId = assigneeData.getString("organizationId");

                            RMCAssignee assignee = new RMCAssignee();
                            assignee.setOrganisationId(assigneeOrgId);
                            assignee.setUserId(assigneeUserId);
                            assigneList.add(assignee);

                        }


                        final RMCAssigner rmcAssigner = new RMCAssigner();
                        rmcAssigner.setOrganisationId(assignerOrgId);
                        rmcAssigner.setPrivelege(assignerPrivelege);
                        rmcAssigner.setUserId(assignerUserId);


                        Gson gson = new Gson();
                        final String detailsStr = gson.toJson(assignmentDetails);

                        final RMCLocation rmcLocation = new RMCLocation();
                        rmcLocation.setAccuracy(accuracy);
                        rmcLocation.setAltitude(altitude);
                        rmcLocation.setLatitude(latitude);
                        rmcLocation.setLongitude(longitude);
                        rmcLocation.setType(locType);

                        RMCAssignment assignment = realm.where(RMCAssignment.class).equalTo("assignmentId", assignmentId)
                                .findFirst();

                        final RMCAssignment assgnmnt = new RMCAssignment();
                        assgnmnt.setAddress(assignmentAddr);
                        assgnmnt.setAssignmentStartTime(assignmentStartTimeInDate);
                        assgnmnt.setAssignmentDeadline(assignmentDeadlineInDateFormat);
                        assgnmnt.setAssignmentId(assignmentId);
                        assgnmnt.setAddedOn(addedOnInDateFormat);
                        assgnmnt.setUpdatedOn(updatedOnInDateFormat);
                        assgnmnt.setAssignmentDetails(detailsStr);
                        assgnmnt.setAssigneeData(assigneList);
                        assgnmnt.setAssignerData(rmcAssigner);
                        assgnmnt.setLocation(rmcLocation);
                        assgnmnt.setPlaceId(placeId);


                        if (assignerUserId.equals(assigneeUserId)) {
                            assgnmnt.setAssignmentType("Self");
                        } else {

                            assgnmnt.setAssignmentType("Manager");
                        }


                        if (assignment != null) {

                            if (assignment.getStatusLog() != null && assignment.getStatusLog().size() > 0) {
                                Log.i("STatus LOg size", "" + assignment.getStatusLog().size());
                                Log.i("STatus LOg size", "" + assignment.getStatusLog().get(0).getType());
//                                RealmList<StatusLog> logs = new RealmList<>();
                                assgnmnt.statusLog.addAll(assignment.getStatusLog());
                            }
                            if (assignment.getLocalStatus() != null && assignment.getLocalStatus().equalsIgnoreCase("Created")) {
                                assgnmnt.setStatus(assignment.getStatus());
                            } else {
                                assgnmnt.setStatus(assignmentStatus);
                            }
                        } else {
                            assgnmnt.setStatus(assignmentStatus);
                        }

                        assgnmnt.setLocalStatus("Downloaded");

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // Add a user
//
//                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//                                String aStartTimeInString = df.format(as);


                                realm.copyToRealmOrUpdate(assgnmnt);
                            }
                        });

                        if (assignmentStatus.equalsIgnoreCase("Assigned") || assignmentStatus.equalsIgnoreCase("Created")) {
                            //updateAssignmentStatus(realm);
                        }

                        if (INFO_LOG == true) {
                            RealmQuery<RMCAssignment> query = realm.where(RMCAssignment.class);
                            RealmResults<RMCAssignment> result = query.findAll();

                            for (RMCAssignment ass : result) {
                                Log.i(getClass().getName() + "RMCAssignment Collection--->", ass.getAssignmentId());
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requestCallback.onResponseReceived(object, true, notificationBody, Constants.downloadAssignmentsCallback);

                realm.close();
            }
        }
    }
}
