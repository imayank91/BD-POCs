package com.RMC.BDCloud.BLE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.RealmDB.Model.RMCBeacon;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RMC.BDCloud.RealmDB.Model.VicinityBeacons;
import com.google.gson.JsonObject;

import org.altbeacon.beacon.Beacon;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Trifork
 * GuestAppSocial
 */
public class Logger implements RequestCallback {

    private BDPreferences prefs;
    private Context context;
    private Realm realm;
    private boolean isMarked = false;
    private String status;

    public Logger(List<Beacon> beacons, Context context) {
        this.context = context;
        if (context != null) {
            prefs = new BDPreferences(context);
            realm = BDCloudUtils.getRealmBDCloudInstance();
            Date date = new Date();
//            long currentTime = date.getTime() - 600000;
            long currentTime = date.getTime() - 1;

            if (currentTime > prefs.getBeaconsLastSeen()) {

                RealmResults<VicinityBeacons> result = realm.where(VicinityBeacons.class).findAll();
                RealmResults<RMCBeacon> rmcBeaconsResult = realm.where(RMCBeacon.class).findAll();

                if (result != null && result.size() > 0) {


                    for (Beacon beacon : beacons) {
                        String buuid = beacon.getId1().toString().toUpperCase();


//                        RMCBeacon results = realm.where(RMCBeacon.class).beginGroup()
//                                .equalTo("uuid", beacon.getId1().toString())
//                                .equalTo("major", beacon.getId2().toString())
//                                .equalTo("minor", beacon.getId3().toString())
//                                .endGroup()
//                                .findFirst();


                        VicinityBeacons vicinityBeacons = realm.where(VicinityBeacons.class)
                                .beginGroup()
                                .equalTo("uuid", buuid)
                                .equalTo("major", beacon.getId2().toString())
                                .equalTo("minor", beacon.getId3().toString())
                                .endGroup()
                                .findFirst();

                        if (vicinityBeacons != null) {
                            if (vicinityBeacons.getUuid().equalsIgnoreCase(beacon.getId1().toString()) &&
                                    vicinityBeacons.getMajor().equalsIgnoreCase(beacon.getId2().toString()) &&
                                    vicinityBeacons.getMinor().equalsIgnoreCase(beacon.getId3().toString())) {
                                Log.i("Vicinity Beacons", vicinityBeacons.getBeaconDetails());
                                UUID uuid = UUID.randomUUID();
                                createRMCBeacon(beacon, uuid, vicinityBeacons.getBeaconId());
                                createBLECheckin(uuid);
                                Date date1 = new Date();
                                prefs.setBeaconsLastSeen(date1.getTime());
                            }
                        }
                    }
                }
            }
        }
        realm.close();
    }

    public Logger(Beacon beacon, String status, Context context) {

        this.status = status;

        this.context = context;
        if (context != null) {
            prefs = new BDPreferences(context);
            realm = BDCloudUtils.getRealmBDCloudInstance();
            Date date = new Date();
//            long currentTime = date.getTime() - 600000;
            long currentTime = date.getTime() - 1;

            if (currentTime > prefs.getBeaconsLastSeen()) {

                RealmResults<VicinityBeacons> result = realm.where(VicinityBeacons.class).findAll();
                RealmResults<RMCBeacon> rmcBeaconsResult = realm.where(RMCBeacon.class).findAll();

                if (result != null && result.size() > 0) {


                    String buuid = beacon.getId1().toString().toUpperCase();


//                        RMCBeacon results = realm.where(RMCBeacon.class).beginGroup()
//                                .equalTo("uuid", beacon.getId1().toString())
//                                .equalTo("major", beacon.getId2().toString())
//                                .equalTo("minor", beacon.getId3().toString())
//                                .endGroup()
//                                .findFirst();


                    VicinityBeacons vicinityBeacons = realm.where(VicinityBeacons.class)
                            .beginGroup()
                            .equalTo("uuid", buuid)
                            .equalTo("major", beacon.getId2().toString())
                            .equalTo("minor", beacon.getId3().toString())
                            .endGroup()
                            .findFirst();

                    if (vicinityBeacons != null) {
                        if (vicinityBeacons.getUuid().equalsIgnoreCase(beacon.getId1().toString()) &&
                                vicinityBeacons.getMajor().equalsIgnoreCase(beacon.getId2().toString()) &&
                                vicinityBeacons.getMinor().equalsIgnoreCase(beacon.getId3().toString())) {
                            Log.i("Vicinity Beacons", vicinityBeacons.getBeaconDetails());
                            UUID uuid = UUID.randomUUID();
                            createRMCBeacon(beacon, uuid, vicinityBeacons.getBeaconId());

                            Date date1 = new Date();
                            prefs.setBeaconsLastSeen(date1.getTime());
                        }
                    }

                }
            }
        }
        realm.close();

    }

    // Checks if external storage is available for read and write.
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Writes data from all beacons in scan range to a csv file in the downloads folder.
    private void logToFile(List<Beacon> beacons) {
//        if (isExternalStorageWritable() && preferences.getBoolean("key_logging", false)) {
        if (isExternalStorageWritable()) {
            String fileName = "BeaconData.csv";
            File beaconDataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
//            CSVWriter writer;

            try {
                if (beaconDataFile.exists() && !beaconDataFile.isDirectory()) {
                    FileWriter fileWriter = new FileWriter(beaconDataFile, true);
                    //                 writer = new CSVWriter(fileWriter);
                } else {
                    //                   writer = new CSVWriter(new FileWriter(beaconDataFile));
                }

                List<String[]> data = new ArrayList<>();
                for (Beacon beacon : beacons) {

                    data.add(new String[]{"UUID", beacon.getId1().toString(),
                            "Major", beacon.getId2().toString(),
                            "Minor", beacon.getId3().toString(),
//                            "Last distance measured", context.getString(R.string.distance, String.format("%.2f", beacon.getDistance())),
                            "Bluetooth Address", beacon.getBluetoothAddress(),
                            "Rssi", String.valueOf(beacon.getRssi()),
                            "TX-power", String.valueOf(beacon.getTxPower()),
                            "Time stamp", SimpleDateFormat.getDateTimeInstance().format(new Date())
                    });
                }

//                writer.writeAll(data);
//
//                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void createRMCBeacon(final Beacon beacon, final UUID uuid, final String beaconId) {

        Date lastSeen;
        String localStatus = null;
        String localStr = null;

        Log.d("createRMCBeacon", "createRMCBeacon");
        realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCBeacon rmcBeacon = realm.where(RMCBeacon.class)
                .beginGroup()
                .equalTo("uuid", beacon.getId1().toString())
                .equalTo("major", beacon.getId2().toString())
                .equalTo("minor", beacon.getId3().toString())
                .endGroup()
                .findFirst();

//        if (rmcBeacon != null) {
//            status = rmcBeacon.getStatus();
//
//            lastSeen = rmcBeacon.getLastseen();
//            isMarked = rmcBeacon.isMarked();
//            localStatus  = rmcBeacon.getStatus();
//
////            if(isMarked == false){
////                Random generator = new Random();
////                int i = generator.nextInt(10) + 1;
////                if(beacon.getId2().toString().equalsIgnoreCase("26871")) {
////                    BDCloudUtils.sendNotification("You have entered RMC's office", context, null, i);
////
////                }
////                isMarked = true;
////            }
//
////            Random generator = new Random();
////            int i = generator.nextInt(10) + 1;
//            if(!status.equalsIgnoreCase(localStatus)) {
//                statusDialog(context,status);
////                BDCloudUtils.sendNotification(status, context, null, 200);
//                localStr = "true";
//            } else{
//                localStr = "false";
//                statusDialog(context,"You have already updated your status");
////                BDCloudUtils.sendNotification("You have already updated your status", context, null, 200);
//            }
//
//
//        } else{
//            localStr = "true";
//            statusDialog(context,status);
////            BDCloudUtils.sendNotification(status, context, null, 200);
//        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RMCBeacon rmcBeacon = new RMCBeacon();
                rmcBeacon.setaId(uuid.toString());
                rmcBeacon.setDistance("" + beacon.getDistance());
                rmcBeacon.setLastseen(new Date());
                rmcBeacon.setMajor("" + beacon.getId2());
                rmcBeacon.setRssi("" + beacon.getRssi());
                rmcBeacon.setMinor("" + beacon.getId3());
                rmcBeacon.setUuid("" + beacon.getId1());
                rmcBeacon.setBeaconId("" + beaconId);
                rmcBeacon.setMarked(isMarked);
                rmcBeacon.setStatus(status);

                realm.copyToRealmOrUpdate(rmcBeacon);
            }
        });

//        if(!localStr.equalsIgnoreCase("false")) {
//            createBLECheckin(uuid);
//        }

    }

    public void createBLECheckin(final UUID uuid) {

        Log.d("BLE Checkin created", "BLE Checkin created");

        if (realm == null) {
            realm = BDCloudUtils.getRealmBDCloudInstance();
        }
        final RMCUser user = realm.where(RMCUser.class).findFirst();
        final RealmResults<RMCBeacon> results = realm.where(RMCBeacon.class).equalTo("aId", uuid.toString()).findAll();

        final RealmList<RMCBeacon> rmcBeacons = new RealmList<RMCBeacon>();
        for (RMCBeacon rmcBeacon : results) {
            rmcBeacons.add(rmcBeacon);
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a checkin
                if (user != null) {
                    JsonObject object = new JsonObject();
                    object.addProperty("beaconStatus",status);
                    RMCCheckin rmcCheckin = new RMCCheckin();
                    rmcCheckin.setLatitude(String.valueOf(prefs.getTransientLatitude()));
                    rmcCheckin.setLongitude(String.valueOf(prefs.getTransientLongitude()));
                    rmcCheckin.setAccuracy(String.valueOf(prefs.getTransientAccuracy()));
                    rmcCheckin.setAltitude(String.valueOf(prefs.getTransientAltitude()));
                    rmcCheckin.setCheckinDetails(object.toString());
                    rmcCheckin.setaIds(uuid.toString());
                    rmcCheckin.setCheckinCategory("Transient");
                    rmcCheckin.setCheckinType("Beacon");
                    rmcCheckin.setRmcBeacons(rmcBeacons);
                    String checkinId = UUID.randomUUID().toString();
                    rmcCheckin.setCheckinId(checkinId);
                    rmcCheckin.setOrganizationId(user.getOrgId());
                    rmcCheckin.setTime(new Date());
                    realm.copyToRealmOrUpdate(rmcCheckin);
                }
            }
        });
    }


    private void statusDialog(Context context, String message) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert!");
        builder.setMessage(message);

        String positiveText = context.getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

//        String negativeText = context.getString(android.R.string.cancel);
//        builder.setNegativeButton(negativeText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // negative button logic
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
//                    }
//                });


        dialog = builder.create();
        // display dialog
        dialog.show();

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

    }
}
