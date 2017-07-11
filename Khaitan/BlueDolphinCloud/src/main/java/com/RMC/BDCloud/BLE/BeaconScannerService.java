package com.RMC.BDCloud.BLE;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.BLE.Scanner;
import com.RMC.BDCloud.BLE.Transmitter;
import com.RMC.BDCloud.RealmDB.Model.RMCBeacon;
import com.RMC.BDCloud.RealmDB.Model.VicinityBeacons;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

import io.realm.RealmResults;

import static com.RMC.BDCloud.Android.BDCloudUtils.DEBUG_LOG;
import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;


/**
 * Created by amresh on 4/3/17.
 */

public class BeaconScannerService extends Service implements Scanner.OnScanBeaconsListener {

    private Scanner scanner;
    private BeaconManager beaconManager;
    private Transmitter transmitter;
    private List<Beacon> beacons = new ArrayList<>();
    private boolean isScanning;
    private RMCBeacon rmcBeacon;
    private Handler handler;
    private BDPreferences prefs;
    private Beacon beacon;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        prefs = new BDPreferences(getApplicationContext());
        // Getting instance of beacon manager.
        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        // Initializing scan service.
        initBeaconScanService();
        // Initializing transmit service.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) initBeaconTransmitService();
        // Disables dragging on switch button
        handler = new Handler();

        toggleScanning();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 120000);

        return Service.START_REDELIVER_INTENT;

    }

    private void toggleScanning() {
        if (!isScanning) startScanning();
        else stopScanning();
    }

    private void startScanning() {

        isScanning = true;
        beaconManager = null;
        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        beaconManager.bind(scanner); // Beacon manager binds the beacon consumer and starts service.

    }

    private void stopScanning() {
        isScanning = false;
        beacons.clear();
        beaconManager.unbind(scanner); // Beacon manager unbinds the beacon consumer and stops service.
    }

    private void initBeaconScanService() {
        scanner = new Scanner(getApplicationContext(), this, beaconManager);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initBeaconTransmitService() {
//        transmitter = new Transmitter(getApplicationContext(), preferences);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onScanBeacons(Collection<Beacon> beacons) {
        Log.i("onScanBeacons", "onScanBeacons");

        Date date = new Date();
        long currentTime = date.getTime() - 120000;


        if (beacons != null && beacons.size() > 0 && (currentTime > prefs.getBeaconsLastSeen())) {

            prefs.setBeaconsLastSeen(date.getTime());

            RealmList<RMCBeacon> rmcBeacons = new RealmList<>();

            rmcBeacon = new RMCBeacon();
            this.beacons = new ArrayList<>();

            Realm realm = BDCloudUtils.getRealmBDCloudInstance();
            for (final Beacon beacon : beacons) {
                final VicinityBeacons vicinityBeacons = realm.where(VicinityBeacons.class)
                        .beginGroup()
                        .equalTo("uuid", beacon.getId1().toString().toUpperCase())
                        .equalTo("major", beacon.getId2().toString())
                        .equalTo("minor", beacon.getId3().toString())
                        .endGroup()
                        .findFirst();

                Log.i("Major - Minor", "" + beacon.getId2() + "-" + beacon.getId3());

                if (vicinityBeacons != null) {

                    String beaconDetails = vicinityBeacons.getBeaconDetails();
                    String address = null;
                    JSONObject object;
                    try {
                        object = new JSONObject(beaconDetails);
                        address = object.getString("address");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (vicinityBeacons.getMajor().equalsIgnoreCase(String.valueOf(beacon.getId2()))
                            && vicinityBeacons.getMinor().equalsIgnoreCase(String.valueOf(beacon.getId3()))) {
                        this.beacons.add(beacon);

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                rmcBeacon.setaId(UUID.randomUUID().toString());
                                rmcBeacon.setDistance("" + beacon.getDistance());
                                rmcBeacon.setLastseen(new Date());
                                rmcBeacon.setMajor("" + beacon.getId2());
                                rmcBeacon.setRssi("" + beacon.getRssi());
                                rmcBeacon.setMinor("" + beacon.getId3());
                                rmcBeacon.setUuid("" + beacon.getId1());
                                rmcBeacon.setBeaconId("" + vicinityBeacons.getBeaconId());
                                rmcBeacon.setMarked(true);
                                rmcBeacon.setStatus("");

                                Log.i("RMC Beacon Added", "" + beacon.getId1() + "-" + beacon.getId2() + "-" + beacon.getId3());

                                realm.copyToRealmOrUpdate(rmcBeacon);
                            }
                        });
                        rmcBeacons.add(rmcBeacon);

                    }
                }
            }

            if (realm != null) {
                realm.close();
            }
            new AttendanceLogger(rmcBeacons, getApplicationContext());
        }
    }
}
