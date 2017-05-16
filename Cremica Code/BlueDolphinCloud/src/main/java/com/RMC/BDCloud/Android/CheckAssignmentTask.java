package com.RMC.BDCloud.Android;

import android.content.Context;

import com.RMC.BDCloud.RealmDB.Model.RMCUser;

import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by mayanksaini on 27/02/17.
 */

public class CheckAssignmentTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private String placeId;
    private String filter;


    public CheckAssignmentTask(RequestCallback requestCallback, Context context, String placeId,
                               String filter) {
        this.requestCallback = requestCallback;
        this.context = context;
        this.placeId = placeId;
        this.filter = filter;
    }

    public void checkAssignment() {

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        String organizationId = user.getOrgId();
        String oauthToken = user.getToken();
        String userId = user.getUserId();
        realm.close();

        String url = Constants.BASE_URL + "organisation/" + organizationId + "/assignment?placeId=" + placeId;

        if (filter != null) {
            url += filter;
        }

        JSONObject jObj;

        try {
            jObj = new JSONObject();
            jObj.put("authorization", oauthToken);
            jObj.put("userId", userId);

            BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService(context,
                    CheckAssignmentTask.this);
            bdCloudPlatformService.executeHttpGet(url, context, Constants.checkAssignmentCallback, jObj);

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.checkAssignmentCallback) {
            if (status == true) {

                if (requestCallback != null) {
                    requestCallback.onResponseReceived(object, status, message, Constants.checkAssignmentCallback);
                }
            }
        }

    }

}
