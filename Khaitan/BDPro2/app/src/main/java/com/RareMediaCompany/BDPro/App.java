package com.RareMediaCompany.BDPro;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.BLE.Logger;
import com.RMC.BDCloud.BLE.Scanner;
import com.RareMediaCompany.BDPro.Activities.MyDashboardActivity;
import com.crashlytics.android.Crashlytics;


import org.json.JSONObject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;


/**
 * Created by mayanksaini on 13/10/15.
 */


public class App extends MultiDexApplication implements RequestCallback {

    private static final String TAG = "BD Pro Beacon";
    Scanner scanner;
    BDPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate", "onCreate");
        Realm.init(this);
        Fabric.with(this, new Crashlytics());
        BDCloudUtils.setContext(this);
        BDCloudUtils.initBaseAppClass(this);

        prefs = new BDPreferences(getApplicationContext());


        Date date = new Date();
        long currentTime = date.getTime() - 120000;
        long lastSeen = prefs.getBeaconsLastSeen();

        if (currentTime > lastSeen) {
            Log.d("Starting to scan", "Starting to scan");
        }

//        scanner = new Scanner(getApplicationContext(), this, beaconManager);
//        beaconManager.bind(scanner);

    }


    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(base);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this);
        }
    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        if (type == Constants.pushNotificationCallback) {
            Log.d("App", "onResponseReceived");
            if (status == true) {
                BDCloudUtils.sendNotification(message, this, MyDashboardActivity.class, 2, null);
//              MyDashboardActivity.mNotifcations.setIcon(R.drawable.notifications_ping);
                Intent updateNewFrag = new Intent("updateAdapter");
                LocalBroadcastManager.getInstance(this).sendBroadcast(updateNewFrag);

            }
        }
    }

}
