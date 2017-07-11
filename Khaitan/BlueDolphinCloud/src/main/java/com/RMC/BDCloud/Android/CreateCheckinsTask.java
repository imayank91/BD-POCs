package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.Holders.CheckinDataHolder;
import com.RMC.BDCloud.Holders.CheckinHolder;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by mayanksaini on 20/10/16.
 */

public class CreateCheckinsTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private JSONObject checkinDetails;
    private CheckinHolder checkinsList;

    public CreateCheckinsTask(Context context, RequestCallback requestCallback, CheckinDataHolder dataHolder) {

        this.requestCallback = requestCallback;
        this.context = context;
        if (this.checkinsList == null) {
            checkinsList = new CheckinHolder();
            checkinsList.data.add(dataHolder);
        }

    }

    public CreateCheckinsTask(Context context, RequestCallback requestCallback, CheckinHolder checkinsList) {
        this.checkinsList = checkinsList;
        this.requestCallback = requestCallback;
        this.context = context;
    }


    public void postCheckins() {
        BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService
                (context, CreateCheckinsTask.this);

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        String organizationId = user.getOrgId();
        String oauthToken = user.getToken();
        String userId = user.getUserId();
        realm.close();

        String url = Constants.BASE_URL + "organisation/" + organizationId + "/checkin";

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(checkinsList);

        JSONObject obj = null;
        JSONObject obj1 = null;
        try {
            obj = new JSONObject(json);
            Log.i("Checkin Payload", "" + obj.toString());
            obj1 = new JSONObject();
            obj1.put("authorization", oauthToken);
            obj1.put("userId", userId);
            if (checkinsList != null) {
                bdCloudPlatformService.executeHttpPost(url, obj, context, Constants.postCheckinsCallback, obj1, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.postCheckinsCallback) {
            if (status == true) {
                try {
                    JSONArray arr = object.getJSONArray("data");
                    JSONObject obj = arr.getJSONObject(0);
                    String code = obj.getString("code");
                    String msg = obj.getString("message");
                    String checkinId = obj.getString("checkinId");

                    if (requestCallback != null) {
                        requestCallback.onResponseReceived(object, status, msg, type);
                    }
                    if (INFO_LOG == true) {
                        Log.i("postCheckinsCallback---> ", code + " " + msg + " " + checkinId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
