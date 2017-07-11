package com.RMC.BDCloud.Android;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.RMC.BDCloud.Holders.AssignmentHolder;
import com.RMC.BDCloud.Holders.BeaconHolder;
import com.RMC.BDCloud.RealmDB.Model.RMCLocation;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RMC.BDCloud.RealmDB.Model.VicinityBeacons;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by amresh on 1/20/17.
 */

public class GetBeaconsTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private Realm realm;
    private Location location;


    public GetBeaconsTask(Context context, RequestCallback requestCallback) {
        this.requestCallback = requestCallback;
        this.context = context;
    }

    public GetBeaconsTask(Context context, RequestCallback requestCallback, Location loc) {
        this.requestCallback = requestCallback;
        this.context = context;
        this.location = loc;
    }

    public void getBeacons() {
        realm = BDCloudUtils.getRealmBDCloudInstance();
        String userId = null, oauthToken;

        RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        userId = user.getUserId();
        oauthToken = user.getToken();


        BDPreferences prefs = new BDPreferences(context);
        String lat = "" + prefs.getTransientLatitude();
        String lon = "" + prefs.getTransientLongitude();
        String loc;
        if (location != null) {
            loc = location.getLatitude() + "," + location.getLongitude();
        } else {
            loc = lat + "," + lon;
        }

        String URL = Constants.BASE_URL + "organisation/" + user.getOrgId() +
                "/beacon?vicinity=" + loc + "&maxDistance=3000";

        realm.close();
        JSONObject jObj;

        try {
            jObj = new JSONObject();
            jObj.put("authorization", oauthToken);
            jObj.put("userId", userId);

            BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService(context,
                    GetBeaconsTask.this);
            bdCloudPlatformService.executeHttpGet(URL, context, Constants.getBeaconsCallback, jObj);

        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        realm = BDCloudUtils.getRealmBDCloudInstance();
        if (type == Constants.getBeaconsCallback) {
            if (status == true) {
                try {
                    JSONObject obj = object.getJSONObject("data");
                    JSONArray arr = obj.getJSONArray("documents");
                    if (arr.length() > 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject beaconData = arr.getJSONObject(i).getJSONObject("beaconData");
                            final JSONObject beaconDetails = arr.getJSONObject(i).getJSONObject("beaconDetails");
                            final String addedOn = beaconData.getString("addedOn");
                            final String updatedOn = beaconData.getString("updatedOn");
                            final String uuid = beaconData.getString("uuid").toUpperCase();
                            final String major = beaconData.getString("major");
                            final String minor = beaconData.getString("minor");
                            final String address = beaconData.getString("address");
                            final String beaconId = beaconData.getString("beaconId");
                            JSONObject location = beaconData.getJSONObject("location");
                            String locType = location.getString("type");
                            String altitude = location.getString("altitude");
                            String accuracy = location.getString("accuracy");
                            JSONArray coordinates = location.getJSONArray("coordinates");
                            String latitude = coordinates.get(1).toString();
                            String longitude = coordinates.get(0).toString();

                            final RMCLocation rmcLocation = new RMCLocation();
                            rmcLocation.setAccuracy(accuracy);
                            rmcLocation.setAltitude(altitude);
                            rmcLocation.setLatitude(latitude);
                            rmcLocation.setLongitude(longitude);
                            rmcLocation.setType(locType);

                            JSONObject associationIds = arr.getJSONObject(i).getJSONObject("associationIds");
                            final String organizationId = associationIds.getString("organizationId");
                            final String placeId = associationIds.getString("placeId");

//                            RealmResults<VicinityBeacons> vicinityBeaconses = realm.where(VicinityBeacons.class).findAll();
//                            if (vicinityBeaconses != null && vicinityBeaconses.size() > 0) {
//                                realm.beginTransaction();
//                                vicinityBeaconses.deleteAllFromRealm();
//                                realm.commitTransaction();
//                            }

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    VicinityBeacons beacons = new VicinityBeacons();
                                    beacons.setUuid(uuid);
                                    beacons.setMajor(major);
                                    beacons.setMinor(minor);
                                    beacons.setAddress(address);
                                    beacons.setAddedOn(addedOn);
                                    beacons.setUpdatedOn(updatedOn);
                                    beacons.setBeaconId(beaconId);
                                    beacons.setLocation(rmcLocation);
                                    beacons.setOrganizationId(organizationId);
                                    beacons.setPlaceId(placeId);
                                    beacons.setBeaconDetails(beaconDetails.toString());
                                    realm.copyToRealmOrUpdate(beacons);
                                }
                            });
                        }

                    }


                } catch (Exception e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
                RealmResults<VicinityBeacons> results = realm.where(VicinityBeacons.class).findAll();

                if (results != null) {
                    for (VicinityBeacons beacons : results) {
                        Log.i("VicinityBeacons", beacons.getUuid());
                        Log.i("VicinityBeacons", beacons.getMajor());
                        Log.i("VicinityBeacons", beacons.getMinor());

                    }
                }
                if (requestCallback != null) {
                    requestCallback.onResponseReceived(object, true, "Successfully saved", Constants.getBeaconsCallback);
                }
            }
        }

    }
}