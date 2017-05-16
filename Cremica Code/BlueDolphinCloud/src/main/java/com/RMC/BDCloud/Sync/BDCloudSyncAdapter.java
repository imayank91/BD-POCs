package com.RMC.BDCloud.Sync;

import android.accounts.Account;
import android.app.ActivityManager;

import android.bluetooth.BluetoothAdapter;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncResult;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.CreateAssignmentTask;
import com.RMC.BDCloud.Android.CreateCheckinsTask;
import com.RMC.BDCloud.Android.DownloadAssignmentTask;
import com.RMC.BDCloud.Android.DynamicObjectsTask;
import com.RMC.BDCloud.Android.GetBeaconsTask;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.Android.UploadImagesTask;
import com.RMC.BDCloud.Android.UserAuthenticationTask;
import com.RMC.BDCloud.BLE.BeaconScannerService;
import com.RMC.BDCloud.Firebase.MyFirebaseInstanceIDService;
import com.RMC.BDCloud.Holders.AssignmentDataHolder;
import com.RMC.BDCloud.Holders.AssignmentHolder;
import com.RMC.BDCloud.Holders.CheckinDataHolder;
import com.RMC.BDCloud.Holders.CheckinHolder;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;

import org.altbeacon.beacon.BeaconManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.RMC.BDCloud.Android.BDCloudUtils.DEBUG_LOG;
import static com.RMC.BDCloud.Android.BDCloudUtils.ERROR_LOG;
import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;
import static com.RMC.BDCloud.Sync.SyncLocation.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.RMC.BDCloud.Sync.SyncLocation.UPDATE_INTERVAL_IN_MILLISECONDS;

/**
 * Created by mayanksaini on 23/11/16.
 */

public class BDCloudSyncAdapter extends AbstractThreadedSyncAdapter implements RequestCallback {

    private ContentResolver mContentResolver;
    private Context context;
    private ContentProviderClient provider;
    private Realm realm;
    private String TAG = "BDCloudSyncAdapter";
    private BDPreferences prefs;
    private BeaconManager beaconManager;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;


    public BDCloudSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
        this.context = context;
        prefs = new BDPreferences(context);
    }


    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public BDCloudSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
    }

    private static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        if (INFO_LOG) {
            Log.i(TAG, "Starting sync");
        }


        try {
            realm = BDCloudUtils.getRealmBDCloudInstance();
        } catch (Exception e) {
//            Realm.init(context);
//            realm = BDCloudUtils.getRealmBDCloudInstance();
        }

        beaconManager = BeaconManager.getInstanceForApplication(context);

        if (!beaconManager.checkAvailability()) {
//            requestBluetooth();
            BluetoothAdapter.getDefaultAdapter().enable();
        }


        realm = BDCloudUtils.getRealmBDCloudInstance();

        if (!isMyServiceRunning(SyncLocation.class, context)) {
            Log.i("inside if", "SyncLocation");
            context.startService(new Intent(context, SyncLocation.class));
        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));

        int hourofday = cal.get(Calendar.HOUR_OF_DAY);

        Log.i("hourofday", "" + hourofday);

        if (hourofday > 7 && hourofday < 21) {
            if (!isMyServiceRunning(BeaconScannerService.class, context)) {
                Log.i("inside if", "BeaconScannerService");
                context.startService(new Intent(context, BeaconScannerService.class));
            }
        }
        RMCUser result = realm.where(RMCUser.class).
                equalTo("isActive", true).
                findFirst();
//        if (prefs.isFirebaseRegistered() == false && result != null) {
//            MyFirebaseInstanceIDService service = new MyFirebaseInstanceIDService();
//            String token = prefs.getFirebaseToken();
//            service.sendRegistrationToServer(token, context);
//        }
        GetBeaconsTask task = new GetBeaconsTask(context, this);
        task.getBeacons();


//        DownloadAssignmentTask assignmentTask = new DownloadAssignmentTask(context,this,null,"");
//        assignmentTask.mayBeDownloadAssignments("Assigned");

        try {


            postAssignments(result);
            mayBeRefreshToken();
            postCurrentCheckin();
            postBLECheckins();
            realm.close();
        } catch (Exception e) {

        }


        if (INFO_LOG) {
            Log.i(TAG, "Sync completed");
        }

    }

    private void requestBluetooth() {
        BDCloudUtils.sendNotification("Please enable your bluetooth", context, null, 205, Settings.ACTION_BLUETOOTH_SETTINGS);
    }

    private void postAssignments(RMCUser rmcUser) {

        if (INFO_LOG) {
            Log.i(TAG, "Posting Assignments");
        }

        RealmResults<RMCAssignment> results = realm.where(RMCAssignment.class).equalTo("localStatus", "Created")
                .findAll();

        if (DEBUG_LOG) {
            Log.d(TAG, "Posting Assignments.Total count - " + results.size());
        }

        for (RMCAssignment assignment : results) {
            AssignmentHolder holder = BDCloudUtils.convertRMCAssignmentToAssignmenHolder(assignment, rmcUser);
            CreateAssignmentTask task = new CreateAssignmentTask(getContext(), this, holder);
            task.createAssignment();
        }

    }

    private void mayBeRefreshToken() {

        RMCUser result = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        long expires = result.getExpires();
        Date date = new Date();
        long currentTime = date.getTime();

        long twoDaysExpiration = expires - 7200000;

        if (currentTime > twoDaysExpiration) {
            if (INFO_LOG) {
                Log.i(TAG, "Old Token - " + result.getToken());
            }
            String email = result.getMobileNo();
            String password = result.getOtpToken();
            String refreshTokenStr = result.getRefreshToken();

            // Todo: Added code to take the organisation id form the project and pass to SDK(to be removed)

            UserAuthenticationTask task = new UserAuthenticationTask(context, this, "true", "custom",
                    email, password, true, refreshTokenStr, prefs.getOrganisationId());
            task.getOauthToken();
        }

    }

    private void postCurrentCheckin() {

        if (INFO_LOG) {
            Log.i(TAG, "postCurrentCheckin");
        }
        //Getting current location
        try {

            RealmQuery<RMCCheckin> query = realm.where(RMCCheckin.class);
            RealmResults<RMCCheckin> result = query
                    .beginGroup()
                    .notEqualTo("checkinType", "Beacon")
                    .notEqualTo("checkinType", "Photo")
                    .endGroup()
                    .findAll();

            if (INFO_LOG) {
                Log.i(TAG, "Posting Non-photo checkins. Total count - " + result.size());
            }
            for (RMCCheckin checkin : result) {
                CheckinHolder checkinHolder = new CheckinHolder();
                ArrayList<CheckinDataHolder> checkinHolderList = new ArrayList<>();
                checkinHolderList.add(BDCloudUtils.convertRMCCheckinToCheckinDataHolder(checkin));
                checkinHolder.data = checkinHolderList;
                CreateCheckinsTask task = new CreateCheckinsTask(getContext(), BDCloudSyncAdapter.this, checkinHolder);
                task.postCheckins();
            }
            RealmQuery<RMCCheckin> query1 = realm.where(RMCCheckin.class);
            RealmResults<RMCCheckin> result1 = query1.equalTo("checkinType", "Photo").isNotNull("imageUrl").findAll();
            if (INFO_LOG) {
                Log.i(TAG, "Posting photo checkins. Total count - " + result1.size());
            }
            for (RMCCheckin checkin : result1) {
                CheckinHolder checkinHolder = new CheckinHolder();
                ArrayList<CheckinDataHolder> checkinHolderList = new ArrayList<>();
                checkinHolderList.add(BDCloudUtils.convertRMCCheckinToCheckinDataHolder(checkin));
                checkinHolder.data = checkinHolderList;
                CreateCheckinsTask task = new CreateCheckinsTask(getContext(), BDCloudSyncAdapter.this, checkinHolder);
                task.postCheckins();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void postBLECheckins() {
        RealmQuery<RMCCheckin> query = realm.where(RMCCheckin.class).equalTo("checkinType", "Beacon");
        RealmResults<RMCCheckin> result = query.findAll();
        if (INFO_LOG) {
            Log.i(TAG, "Posting BLE checkins. Total count - " + result.size());
        }
        for (RMCCheckin checkin : result) {
            CheckinHolder checkinHolder = new CheckinHolder();
            ArrayList<CheckinDataHolder> checkinHolderList = new ArrayList<>();
            checkinHolderList.add(BDCloudUtils.convertRMCCheckinToCheckinDataHolder(checkin));
            checkinHolder.data = checkinHolderList;
            CreateCheckinsTask task = new CreateCheckinsTask(getContext(), BDCloudSyncAdapter.this, checkinHolder);
            task.postCheckins();
        }

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        if (type == Constants.postCheckinsCallback) {
            if (status == true) {
                Realm realm = BDCloudUtils.getRealmBDCloudInstance();
                try {
                    JSONArray arr = object.getJSONArray("data");
                    JSONObject obj = arr.getJSONObject(0);
                    String code = obj.getString("code");
                    String msg = obj.getString("message");
                    String checkinId = obj.getString("checkinId");


                    if (msg != null && (msg.equalsIgnoreCase("Checkin Saved") || msg.equalsIgnoreCase("Checkin already posted in the system"))) {
                        final RMCCheckin checkin = realm.where(RMCCheckin.class).equalTo("checkinId", checkinId).findFirst();


                        if (checkin != null) {
                            if (INFO_LOG == true) {
                                Log.i(TAG, "Deleting checkin Id= " + checkin.getCheckinId());
                            }
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    // Add a user
                                    checkin.deleteFromRealm();
                                }
                            });
                            if (INFO_LOG == true) {
                                Log.i(TAG, "checkin successfully deleted");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
            }
        } else if (type == Constants.uploadPhotoCallback) {
            if (status == true) {
                try {
                    String checkinId = object.getString("checkinId");
                    String imageName = object.getString("imageName");
                    final String url = "d27al5ltjawswh.cloudfront.net/" + imageName;
                    final RealmResults<RMCCheckin> result = realm.where(RMCCheckin.class).equalTo("checkinId", checkinId)
                            .findAll();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            result.get(0).setImageUrl(url);
                            realm.copyToRealmOrUpdate(result);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (type == Constants.Oauth2Callback) {
            if (status == true) {
                if (INFO_LOG) {
                    Log.i(TAG, "Token Successfully Refreshed");
                }
                realm = BDCloudUtils.getRealmBDCloudInstance();
                RMCUser result = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
                if (INFO_LOG) {
                    Log.i(TAG, "New Token - " + result.getToken());
                }
                realm.close();
            } else if (type == Constants.getBeaconsCallback) {
                if (status == true) {
                    if (INFO_LOG) {
                        Log.i(TAG, "getBeaconsCallback - " + message);
                    }
                }
            }
        }
    }
}
