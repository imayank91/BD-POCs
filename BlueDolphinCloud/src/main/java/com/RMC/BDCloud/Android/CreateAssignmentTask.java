package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.Holders.AssignmentHolder;
import com.RMC.BDCloud.Holders.CheckinHolder;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignee;
import com.RMC.BDCloud.RealmDB.Model.RMCAssigner;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCLocation;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by mayanksaini on 24/10/16.
 */

public class CreateAssignmentTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private AssignmentHolder assignmentHolder;
    private Realm realm;
    private Date assignmentDeadlineInDateFormat, addedOnInDateFormat, updatedOnInDateFormat, startTimeInDateFormat,
            timeInDateFormat;

    public CreateAssignmentTask(Context context, RequestCallback requestCallback, AssignmentHolder assignmentHolder) {
        this.assignmentHolder = assignmentHolder;
        this.requestCallback = requestCallback;
        this.context = context;
    }

    public void createAssignment() {
        BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService
                (context, CreateAssignmentTask.this);

        realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        String organizationId = user.getOrgId();
        String oauthToken = user.getToken();
        String userId = user.getUserId();
        realm.close();


        String url = Constants.BASE_URL + "organisation/" + organizationId + "/assignment";

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(assignmentHolder);


        JSONObject obj = null;
        JSONObject object = null;
        try {
            obj = new JSONObject(json);
            object = new JSONObject();

            object.put("authorization", oauthToken);
            object.put("userId", userId);

            Log.i("ass payload", "" + obj.toString());

            if (assignmentHolder != null) {
                bdCloudPlatformService.executeHttpPost(url, obj, context, Constants.createAssignmentCallback, object, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveLocalCopytoDB() {
        realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        String userId = user.getUserId();

        final RMCLocation location = new RMCLocation();
        location.setLatitude(assignmentHolder.data.get(0).latitude);
        location.setLongitude(assignmentHolder.data.get(0).longitude);
        location.setAccuracy(assignmentHolder.data.get(0).accuracy);
        location.setAltitude(assignmentHolder.data.get(0).altitude);

//        final RMCAssignee assignee = new RMCAssignee();
//        assignee.setOrganisationId(assignmentHolder.data.get(0).organizationId);
//        assignee.setUserId(userId);
        final RealmList<RMCAssignee> assigneesList = new RealmList<>();

        ArrayList<String> assigneeIDs = assignmentHolder.data.get(0).assigneeIds;

        if (assigneeIDs != null && assigneeIDs.size() > 0) {
            for (String str : assigneeIDs) {
                final RMCAssignee assignee = new RMCAssignee();
                assignee.setOrganisationId(assignmentHolder.data.get(0).organizationId);
                assignee.setUserId(str);
                assigneesList.add(assignee);
            }
        }


        final RMCAssigner assigner = new RMCAssigner();
        assigner.setOrganisationId(assignmentHolder.data.get(0).organizationId);
        assigner.setUserId(userId);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            assignmentDeadlineInDateFormat = format.parse(assignmentHolder.data.get(0).assignmentDeadline);
            addedOnInDateFormat = format.parse(assignmentHolder.data.get(0).addedOn);
            updatedOnInDateFormat = format.parse(assignmentHolder.data.get(0).updatedOn);
            startTimeInDateFormat = format.parse(assignmentHolder.data.get(0).assignmentStartTime);
            timeInDateFormat = format.parse(assignmentHolder.data.get(0).time);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RMCAssignment assignment = new RMCAssignment();
                assignment.setAddress(assignmentHolder.data.get(0).assignmentAddress);
                assignment.setStatus(assignmentHolder.data.get(0).status);
                assignment.setAssignmentId(assignmentHolder.data.get(0).assignmentId);
                assignment.setAssignmentStartTime(startTimeInDateFormat);
                assignment.setAddedOn(addedOnInDateFormat);
                assignment.setTime(timeInDateFormat);
                assignment.setAssignmentDeadline(assignmentDeadlineInDateFormat);
                assignment.setUpdatedOn(updatedOnInDateFormat);
                assignment.setAssignmentDetails(assignmentHolder.data.get(0).assignmentDetails.toString());
                assignment.setAssigneeData(assigneesList);
                assignment.setAssignerData(assigner);
                assignment.setLocation(location);
                assignment.setAssignmentType("Self");
                assignment.setLocalStatus("Created");
                realm.copyToRealmOrUpdate(assignment);
            }
        });

        realm.close();
    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        if (type == Constants.createAssignmentCallback) {
            if (status == true) {
                try {
                    JSONArray arr = object.getJSONArray("data");
                    JSONObject obj = arr.getJSONObject(0);
                    String code = obj.getString("code");
                    String msg = obj.getString("message");
                    String assignmentId = obj.getString("assignmentId");

                    requestCallback.onResponseReceived(object, status, null, type);

                    if (INFO_LOG == true) {
                        Log.i("CreateAssignmentTask", code + " " + msg + " " + assignmentId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
