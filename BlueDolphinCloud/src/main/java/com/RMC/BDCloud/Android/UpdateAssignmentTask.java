package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.Holders.AssignmentDataHolder;
import com.RMC.BDCloud.Holders.AssignmentHolder;
import com.RMC.BDCloud.Holders.CheckinDataHolder;
import com.RMC.BDCloud.Holders.CheckinHolder;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by amresh on 3/21/17.
 */

public class UpdateAssignmentTask implements RequestCallback{

    private RequestCallback requestCallback;
    private Context context;
    private JSONObject assignmentDetail;
    private AssignmentHolder assignmentList;

    public UpdateAssignmentTask(Context context, RequestCallback requestCallback, AssignmentDataHolder assignmentDataHolder){

        this.requestCallback = requestCallback;
        this.context = context;
        if(this.assignmentList == null){
            assignmentList = new AssignmentHolder();
            assignmentList.data.add(assignmentDataHolder);
        }

    }

    public UpdateAssignmentTask(Context context, RequestCallback requestCallback, AssignmentHolder assignmentList){
        this.assignmentList = assignmentList;
        this.requestCallback = requestCallback;
        this.context = context;
    }



    public void putAssignment(){
        BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService
                (context, UpdateAssignmentTask.this);

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive",true).findFirst();
        String organizationId = user.getOrgId();
        String oauthToken = user.getToken();
        String userId = user.getUserId();
        realm.close();

        String url = Constants.BASE_URL +"organisation/"+organizationId+"/assignment";

        Gson gson  = new GsonBuilder().create();
        String json = gson.toJson(assignmentList);

        JSONObject obj = null;
        JSONObject obj1 = null;
        try {
            obj  = new JSONObject(json);
            obj1  = new JSONObject();
            obj1.put("authorization",oauthToken);
            obj1.put("userId",userId);
            if(assignmentList != null) {
                bdCloudPlatformService.executeHttpPut(url,obj,context,Constants.updateAssignmentCallback,obj1,null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.updateAssignmentCallback) {
            if (status == true) {
                try {
                    JSONArray arr = object.getJSONArray("data");
                    JSONObject obj = arr.getJSONObject(0);
                    String code = obj.getString("code");
                    String msg = obj.getString("message");
                    String checkinId = obj.getString("assignmentId");

                    if (requestCallback != null) {
                        requestCallback.onResponseReceived(object, status, msg, type);
                    }
                    if (INFO_LOG == true) {
                        Log.i("putAssignmentCallback---> ", code+" "+ msg +" " + checkinId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
