package com.RMC.BDCloud.BLE;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.RealmDB.Model.RMCBeacon;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by mayanksaini on 04/04/17.
 */


public class AttendanceLogger {

    private Realm realm;
    private BDPreferences prefs;
    private String TAG = "AttendanceLogger";

    public AttendanceLogger(RealmList<RMCBeacon> rmcBeacons, Context context) {

        if (realm == null) {
            realm = BDCloudUtils.getRealmBDCloudInstance();
        }

        if (prefs == null) {
            prefs = new BDPreferences(context);
        }

        if (rmcBeacons != null && rmcBeacons.size() > 0) {
            long fiveMinsBackDate = new Date().getTime() - 300000;

            for (final RMCBeacon rmcBeacon : rmcBeacons) {
                long lastSeen = rmcBeacon.getLastseen().getTime();

                if (lastSeen < fiveMinsBackDate) {
                    updateStatus(rmcBeacon, "Out");
                }
            }

            Date date = new Date();
            long currentTime = date.getTime() - 300000;

            if (currentTime > prefs.getBeaconsLastCheckin()) {
                createBLECheckin(rmcBeacons, context);
//            deleteRMCBeacons(rmcBeacons);
            }
        }

    }

    public void deleteRMCBeacons(final RealmList<RMCBeacon> rmcBeacons) {
        for (final RMCBeacon beacon : rmcBeacons) {
            final RMCBeacon rmcBeacon = realm.where(RMCBeacon.class).equalTo("beaconId", beacon.getBeaconId()).findFirst();
            if (rmcBeacon != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        rmcBeacon.deleteFromRealm();
                    }
                });
            }
        }
    }

    public void createBLECheckin(final RealmList<RMCBeacon> rmcBeacon, Context context) {

        final RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        final BDPreferences prefs = new BDPreferences(context);


        for (RMCBeacon beacon : rmcBeacon) {
            Log.i(TAG, "BLE Checkin Created for Major- " + beacon.getMajor() + " Minor-" + beacon.getMinor());
        }




        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a checkin
                if (user != null) {
                    JsonObject object = new JsonObject();
                    RMCCheckin rmcCheckin = new RMCCheckin();
                    rmcCheckin.setLatitude(String.valueOf(prefs.getTransientLatitude()));
                    rmcCheckin.setLongitude(String.valueOf(prefs.getTransientLongitude()));
                    rmcCheckin.setAccuracy(String.valueOf(prefs.getTransientAccuracy()));
                    rmcCheckin.setAltitude(String.valueOf(prefs.getTransientAltitude()));
                    rmcCheckin.setCheckinDetails(object.toString());
                    rmcCheckin.setCheckinCategory("Transient");
                    rmcCheckin.setCheckinType("Beacon");
                    rmcCheckin.setRmcBeacons(rmcBeacon);
                    String checkinId = UUID.randomUUID().toString();
                    rmcCheckin.setCheckinId(checkinId);
                    rmcCheckin.setaIds(checkinId);
                    rmcCheckin.setOrganizationId(user.getOrgId());
                    rmcCheckin.setTime(new Date());
                    realm.copyToRealmOrUpdate(rmcCheckin);
                }
            }
        });


        if(BDCloudUtils.DEBUG_LOG) {
          for(RMCBeacon beacon: rmcBeacon) {
              Log.d(TAG,"BLE Checkin created for Major - "+ beacon.getMajor() + " Minor-" + beacon.getMinor());
          }
        }
        Date date = new Date();
        prefs.setBeaconsLastCheckin(date.getTime());

    }

    public void updateStatus(final RMCBeacon rmcBeacon, final String status) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                rmcBeacon.setStatus(status);
                realm.copyToRealmOrUpdate(rmcBeacon);
            }
        });
    }
}