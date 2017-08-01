package com.RMC.BDCloud.Android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by mayanksaini on 07/07/17.
 */

public class GpsLocationReceiver extends BroadcastReceiver {

    private Realm realm;
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.i("GpsLocationReceiver","onReceive");
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //do something
                JsonObject object = new JsonObject();
                object.addProperty("gpsStatus",true);
                createGPSStatusCheckin(context, object.toString());
                Log.d("GpsLocationReceiver","gps on");

            } else {
                JsonObject object = new JsonObject();
                object.addProperty("gpsStatus",false);
                createGPSStatusCheckin(context, object.toString());
                Log.d("GpsLocationReceiver","gps off");

                //do something else
            }


        }
    }


    private void createGPSStatusCheckin(Context context, final String checkinDetails) {
        realm = BDCloudUtils.getRealmBDCloudInstance();
        final RMCUser user = realm.where(RMCUser.class).findFirst();
        final BDPreferences prefs = new BDPreferences(context);
        final String checkinId = UUID.randomUUID().toString();
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
                    rmcCheckin.setCheckinDetails(checkinDetails);
                    rmcCheckin.setCheckinCategory("Data");
                    rmcCheckin.setCheckinType("Data");
                    rmcCheckin.setCheckinId(checkinId);
                    rmcCheckin.setOrganizationId(user.getOrgId());
                    rmcCheckin.setTime(new Date());
                    realm.copyToRealmOrUpdate(rmcCheckin);
                }
            }
        });

        realm.close();

    }
}
