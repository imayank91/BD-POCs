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

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;


/**
 * Created by mayanksaini on 13/10/15.
 */


public class App extends MultiDexApplication implements RequestCallback, Scanner.OnScanBeaconsListener, BootstrapNotifier, BeaconConsumer, RangeNotifier {

    private RegionBootstrap regionBootstrap;
    BeaconManager beaconManager;
    private static final String TAG = "BD Pro Beacon";
    Scanner scanner;
    Region region;
    BDPreferences prefs;
    private BackgroundPowerSaver backgroundPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate", "onCreate");
        Realm.init(this);
        Fabric.with(this, new Crashlytics());
        BDCloudUtils.setContext(this);
        BDCloudUtils.initBaseAppClass(this);

        prefs = new BDPreferences(getApplicationContext());

//        if (beaconManager == null) {
        if (beaconManager == null) {
//
//            beaconManager = BeaconManager.getInstanceForApplication(this);
//            //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
//
//            Region region = new Region("backgroundRegion", null, null, null);
//            regionBootstrap = new RegionBootstrap(this, region);
//
//            backgroundPowerSaver = new BackgroundPowerSaver(this);
//
//            beaconManager.setBackgroundBetweenScanPeriod(120000);
//            beaconManager.setForegroundBetweenScanPeriod(120000);
//            beaconManager.bind(this);
//        }
//
        }

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
                BDCloudUtils.sendNotification(message, this, MyDashboardActivity.class, 2,null);
//              MyDashboardActivity.mNotifcations.setIcon(R.drawable.notifications_ping);
                Intent updateNewFrag = new Intent("updateAdapter");
                LocalBroadcastManager.getInstance(this).sendBroadcast(updateNewFrag);

            }
        }
    }


    @Override
    public void onScanBeacons(final Collection<Beacon> beacons) {

        Log.d("onScanBeacons", "onScanBeacons");
//        Log.d("onScanBeacons", "onScanBeacons");
//        if (beacons != null && beacons.size() > 0) {
//            List<Beacon> beaconsList = (List<Beacon>) beacons;
//            beaconManager.unbind(scanner);
//            //beaconManager.stopRangingBeaconsInRegion(region);
//            regionBootstrap.disable();
//            new Logger(beaconsList, getApplicationContext());
//            Date date = new Date();
//            prefs.setBeaconsLastSeen(date.getTime());
//
//
//        }
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 5000);

    }

    @Override
    public void onBeaconServiceConnect() {
//        beaconManager.setRangeNotifier(this);
//        try {
//            beaconManager.updateScanPeriods();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "did enter region.");

//        beaconManager.unbind(scanner);
//
//        try {
//            beaconManager.startRangingBeaconsInRegion(region);
//        } catch (RemoteException e) {
//            if (BuildConfig.DEBUG) Log.d(TAG, "Can't start ranging");
//        }
    }

    @Override
    public void didExitRegion(Region region) {
//        try {
//            beaconManager.stopRangingBeaconsInRegion(region);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.d(TAG, "I have just switched from seeing/not seeing beacons: " + i);

    }
//        try {
//            beaconManager.updateScanPeriods();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }


    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
//        if (beacons.size() > 0) {
//            for (Beacon b : beacons) {
//                Log.i("Beacons Major-Minor", b.getId2() + "-" + b.getId3());
//            }
//        }
    }
}