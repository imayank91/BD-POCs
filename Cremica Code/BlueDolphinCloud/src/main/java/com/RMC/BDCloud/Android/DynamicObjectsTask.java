package com.RMC.BDCloud.Android;

import android.content.Context;

import com.RMC.BDCloud.RealmDB.Model.RMCUser;

import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by mayanksaini on 28/02/17.
 */

public class DynamicObjectsTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private String dId;
    private String filter;
    private int overrideDobjectCallback;


    public DynamicObjectsTask(RequestCallback requestCallback, Context context, String dId, String filter, int dObjectCallback) {
        this.requestCallback = requestCallback;
        this.context = context;
        this.dId = dId;
        this.filter = filter;
        this.overrideDobjectCallback = dObjectCallback;
    }

    public void downloadDObjects() {

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        String organizationId = user.getOrgId();
        String oauthToken = user.getToken();
        String userId = user.getUserId();
        realm.close();

        String url = Constants.BASE_URL + "organisation/" + organizationId + "/d";

        if(dId != null && !dId.equals("")) {
            url+= "/"+dId;
        }

        if (filter != null) {
            url += filter;
        }

        JSONObject jObj;

        try {
            jObj = new JSONObject();
            jObj.put("authorization", oauthToken);
            jObj.put("userId", userId);

            BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService(context,
                    DynamicObjectsTask.this);

            bdCloudPlatformService.executeHttpGet(url, context, Constants.downloadDObjectsCallback, jObj);

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        if (type == Constants.downloadDObjectsCallback) {
            if (status == true) {
                if (overrideDobjectCallback == 0) {
                    requestCallback.onResponseReceived(object, status, message, type);
                }else{
                    type = overrideDobjectCallback;
                    requestCallback.onResponseReceived(object, status, message, type);
                }
            }
        }
    }
}
