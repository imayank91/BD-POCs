package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by mayanksaini on 27/04/17.
 */

public class DownloadPlacesTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private String checkinId;
    private Realm realm;
    private JsonArray placeIds;

    public DownloadPlacesTask(Context context, RequestCallback requestCallback, String checkinId) {
        this.requestCallback = requestCallback;
        this.context = context;
        this.checkinId = checkinId;
    }

    public void downloadPlaces() {
        BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService
                (context, DownloadPlacesTask.this);

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        String organizationId = user.getOrgId();
        String oauthToken = user.getToken();
        String userId = user.getUserId();
        realm.close();

        BDPreferences preferences = new BDPreferences(context);

        String latitude = "" + preferences.getTransientLatitude();
        String longitude = "" + preferences.getTransientLongitude();

        String url = Constants.BASE_URL + "organisation/" + organizationId + "/place?vicinity=" + latitude + "," + longitude + "&maxDistance=25";

        Log.i("DownloadPlacesTask ", " " + url);

        JSONObject object = new JSONObject();
        try {
            object.put("authorization", oauthToken);
            object.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bdCloudPlatformService.executeHttpGet(url, context, Constants.downloadPlaceCallback, object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        if (type == Constants.downloadPlaceCallback) {
            if (status == true) {
                placeIds = new JsonArray();
                try {
                    JSONObject data = object.getJSONObject("data");
                    JSONArray documents = data.getJSONArray("documents");

                    for (int i = 0; i < documents.length(); i++) {
                        JSONObject docs = documents.getJSONObject(i);
                        JSONObject placeData = docs.getJSONObject("placeData");
                        String placeId = placeData.getString("placeId");
                        placeIds.add(placeId);

                    }

                    realm = BDCloudUtils.getRealmBDCloudInstance();
                    final RMCCheckin checkin = realm.where(RMCCheckin.class).equalTo("checkinId", checkinId).findFirst();
                    Gson gson = new GsonBuilder().create();

                    String json = gson.toJson(placeIds);
//                    JsonParser parser = new JsonParser();
//
//                    JsonObject placeIds =  parser.parse(json).getAsJsonObject();
                    JsonObject checkinObject = new JsonObject();
                    checkinObject.add("placeIds",placeIds);
                    final String checkinJSON = gson.toJson(checkinObject);

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            checkin.setCheckinDetails(checkinJSON);
                            realm.copyToRealmOrUpdate(checkin);
                        }
                    });
                    realm.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
