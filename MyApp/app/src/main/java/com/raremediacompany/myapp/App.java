package com.raremediacompany.myapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.BLE.BeaconScannerService;
import com.crashlytics.android.Crashlytics;
import com.raremediacompany.myapp.Activities.MainActivity;

import io.fabric.sdk.android.Fabric;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by amresh on 4/19/17.
 */

public class App extends MultiDexApplication implements RequestCallback,BootstrapNotifier{

    private String TAG ="App Beacon Manager";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Realm.init(this);

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
         beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("com.raremediacompany.bdattendance", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

    }

    @Override
    protected void attachBaseContext(Context base) {

        super.attachBaseContext(base);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this);
        }
       // BDCloudUtils.setContext(getApplicationContext());
       // BDCloudUtils.initBaseAppClass(this);

        //BDCloudUtils.startSync(base);
    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.pushNotificationCallback) {
            Log.d("App","onResponseReceived");
            if (status == true) {
                //BDCloudUtils.sendNotification(message, this, MainActivity.class, 1);

            }
        }
        //BDCloudUtils.sendNotification(message, this, MainActivity.class,1);
    }



    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "Got a didEnterRegion call");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        regionBootstrap.disable();

        getApplicationContext().startService(new Intent(App.this, BeaconScannerService.class));

    }

    @Override
    public void didExitRegion(Region region) {
        Log.i("didExitRegion","didExitRegion");
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.i("didDetermineStateForRegion","didDetermineStateForRegion");

    }
}
