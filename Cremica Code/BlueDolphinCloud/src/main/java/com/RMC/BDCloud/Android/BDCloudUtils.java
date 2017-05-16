package com.RMC.BDCloud.Android;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.RMC.BDCloud.Holders.AssignmentDataHolder;
import com.RMC.BDCloud.Holders.AssignmentHolder;
import com.RMC.BDCloud.Holders.BeaconHolder;
import com.RMC.BDCloud.Holders.CheckinDataHolder;
import com.RMC.BDCloud.R;
import com.RMC.BDCloud.RealmDB.Migration;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignee;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCBeacon;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RMC.BDCloud.RealmDB.Module.AllCloudModule;
import com.RMC.BDCloud.Sync.SyncLocation;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import io.realm.DynamicRealm;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by mayanksaini on 19/10/16.
 */

public class BDCloudUtils {

    public static boolean DEBUG_LOG = true;
    public static boolean ERROR_LOG = true;
    public static boolean INFO_LOG = true;
    private static Context currentContext = null;
    public static AmazonS3 s3 = null;
    public static TransferUtility utility = null;
    public static CognitoCachingCredentialsProvider credentialsProvider = null;
    private static RequestCallback requestCallback = null;

    public static BDCloudPlatformServiceAndroid getPlatformService(Context context, RequestCallback object) {
        BDCloudPlatformServiceAndroid platformServiceAndroid = new BDCloudPlatformServiceAndroid();
        platformServiceAndroid.init(context, object);
        return platformServiceAndroid;
    }

    public static Context setContext(Context context) {
        if (currentContext == null) {
            currentContext = context;
        }
        return currentContext;
    }

    public static void init(@NonNull Context context, @NonNull String secretKey, @NonNull String orgId,
                            @NonNull String firstName, String lastName, @NonNull String email, String mobile) {

        if (context == null) {
            throw new IllegalArgumentException("context can't be null");
        }
        if (secretKey == null || secretKey.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("secretKey can't be null or empty");
        }
        if (orgId == null || orgId.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("orgId can't be null or empty");
        }
        if (firstName == null || firstName.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("firstName can't be null or empty");
        }
        if (email == null || email.equalsIgnoreCase("")) {
            throw new IllegalArgumentException("email can't be null or empty");
        }
//        Realm.init(context);
        InitialiseSDKTask task = new InitialiseSDKTask(context, null, secretKey, orgId, firstName,
                lastName, mobile, email);

        task.registerSDK();
    }

    public static Realm getRealmBDCloudInstance() {

        RealmConfiguration bdCloudConfig = new RealmConfiguration.Builder()
                .name(Constants.BDCLOUD_SCHEMA)
                .schemaVersion(Constants.BDCLOUD_SCHEMA_VERSION)
                .migration(new Migration())
                .modules(Realm.getDefaultModule(), new AllCloudModule())
                .build();
//        Realm.setDefaultConfiguration(bdCloudConfig);
//        try {
//            Realm.migrateRealm(bdCloudConfig, new Migration());
//        } catch (FileNotFoundException ignored) {
//            // If the Realm file doesn't exist, just ignore.
//        }

        Realm defaultRealm = Realm.getInstance(bdCloudConfig);
        return defaultRealm;
    }


    public static synchronized RequestCallback initBaseAppClass(RequestCallback callback) {
        if (requestCallback == null) {
//            if (callback == null) {
//                throw new IllegalArgumentException("Non-null context required.");
//            }
            requestCallback = callback;
        }
        return requestCallback;
    }

    public static synchronized AmazonS3 getS3(CognitoCachingCredentialsProvider credentialsProvider) {
        if (s3 == null) {
            s3 = new AmazonS3Client(credentialsProvider);
        }
        return s3;
    }

    public static synchronized TransferUtility getTransferService(Context context, AmazonS3 s3) {
        if (utility == null) {
            utility = new TransferUtility(s3, context);
        }
        return utility;
    }


    public static synchronized CognitoCachingCredentialsProvider getCredentialsProvider(Context context) {

        if (credentialsProvider == null) {
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    Constants.IDENTITY_POOL_ID, // Identity Pool ID
                    Regions.AP_NORTHEAST_1 // Region
            );
        }

        return credentialsProvider;
    }

    public static String getDeviceId(Context context) {
        String device_id = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            device_id = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device_id;
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            if (e != null)
                e.printStackTrace();
            //Handle exception
        }
        return null;
    }

    public static AssignmentHolder convertRMCAssignmentToAssignmenHolder(RMCAssignment assignment
            , RMCUser rmcUser) {

        AssignmentHolder aHolder = new AssignmentHolder();
        aHolder.data = new ArrayList<>();

        AssignmentDataHolder holder = new AssignmentDataHolder();

        holder.organizationId = rmcUser.getOrgId();
        holder.assignmentId = assignment.getAssignmentId();

        ArrayList<String> assigneeIds = new ArrayList<>();
        if (assignment.getAssigneeData() != null && assignment.getAssigneeData().size() > 0) {
            for (RMCAssignee assignee : assignment.getAssigneeData()) {
                assigneeIds.add(assignee.getUserId());
            }
        }
        holder.assigneeIds = assigneeIds;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");        /// STRING TO DATE

        format.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

        String addedOn = format.format(assignment.getAddedOn());
        String time = format.format(assignment.getTime());
        String assignmentDeadline = format.format(assignment.getAssignmentDeadline());
        String assignmentStartTime = format.format(assignment.getAssignmentStartTime());
        String updatedOn = format.format(assignment.getUpdatedOn());

        holder.addedOn = addedOn;
        holder.time = time;
        holder.status = "Assigned";
        holder.assignmentAddress = assignment.getAddress();
        holder.assignmentDeadline = assignmentDeadline;
        holder.assignmentStartTime = assignmentStartTime;
        holder.updatedOn = updatedOn;
        holder.statusLog = "";
        holder.latitude = assignment.getLocation().getLatitude();
        holder.longitude = assignment.getLocation().getLongitude();
        holder.altitude = assignment.getLocation().getAltitude();
        holder.accuracy = assignment.getLocation().getAccuracy();

        try {
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(assignment.getAssignmentDetails()).getAsJsonObject();
//            String versionName = BDCloudUtils.getPackageInfo(BDCloudUtils.setContext(null)).versionName;
//            object.addProperty("versionName", versionName);
//            object.addProperty("userAgent", "Android");
            holder.assignmentDetails = object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        aHolder.data.add(holder);
        return aHolder;

    }

    public static CheckinDataHolder convertRMCCheckinToCheckinDataHolder(RMCCheckin rmcCheckin) {

        CheckinDataHolder holder = new CheckinDataHolder();
        holder.latitude = rmcCheckin.getLatitude();
        holder.longitude = rmcCheckin.getLongitude();
        holder.accuracy = rmcCheckin.getAccuracy();
        holder.altitude = rmcCheckin.getAltitude();
        holder.organizationId = rmcCheckin.getOrganizationId();
        Log.i("CheckinDataHolder", holder.organizationId);

        holder.checkinId = rmcCheckin.getCheckinId();

        try {
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(rmcCheckin.getCheckinDetails()).getAsJsonObject();
            //String versionName = BDCloudUtils.getPackageInfo(BDCloudUtils.setContext(null)).versionName;
            //  object.addProperty("versionName", versionName);
            holder.checkinDetails = object;

            if (rmcCheckin.getCheckinId().equalsIgnoreCase("8eff79cc-91e3-4232-a940-4d491d08ef02")) {
                Log.i("Checkin details", object.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.time = rmcCheckin.getTime().toString();
        holder.checkinCategory = rmcCheckin.getCheckinCategory();
        holder.checkinType = rmcCheckin.getCheckinType();
        holder.assignmentId = rmcCheckin.getAssignmentId();
        holder.imageUrl = rmcCheckin.getImageUrl();

        RealmList<RMCBeacon> rmcBeacons = rmcCheckin.getRmcBeacons();
        ArrayList<BeaconHolder> beaconHolders = new ArrayList<>();
        for (RMCBeacon beacon : rmcBeacons) {
            BeaconHolder bHolder = new BeaconHolder();
            bHolder.aId = beacon.getaId();
            bHolder.uuid = beacon.getUuid();
            bHolder.distance = beacon.getDistance();
            bHolder.lastseen = beacon.getLastseen().toString();
            bHolder.rssi = beacon.getRssi();
            bHolder.major = beacon.getMajor();
            bHolder.minor = beacon.getMinor();
            bHolder.beaconId = beacon.getBeaconId();

            if (beacon.getStatus() != null) {

            }
            beaconHolders.add(bHolder);
        }

        if (beaconHolders != null && beaconHolders.size() > 0) {

            JsonArray result = (JsonArray) new Gson().toJsonTree(beaconHolders,
                    new TypeToken<List<BeaconHolder>>() {
                    }.getType());
            holder.beaconProximities = result;
        } else {
            holder.beaconProximities = new JsonArray();
        }

        return holder;
    }

    public static Account startSync(Context context) {
        Account mAccount = null;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Account[] accounts = AccountManager.get(context).getAccountsByType(context.getResources().getString(R.string.sync_account_type));
            if (0 >= accounts.length)
                mAccount = createSyncAccount(context);
            else {
                mAccount = accounts[0];
                if (INFO_LOG) {
                    Log.i("BDCloudUtils-startSync", "Sync Account exists");
                }
                boolean isEnabled = ContentResolver.getSyncAutomatically(mAccount, context.getResources().getString(R.string.content_authority));
                if (!isEnabled) {
                    ContentResolver.addPeriodicSync(mAccount, context.getResources().getString(R.string.content_authority), new Bundle(), Constants.SYNC_FREQUENCY);
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                Account[] accounts = AccountManager.get(context).getAccountsByType(context.getResources().getString(R.string.sync_account_type));
                if (0 >= accounts.length)
                    mAccount = createSyncAccount(context);
                else {
                    mAccount = accounts[0];
                    if (INFO_LOG) {
                        Log.i("BDCloudUtils-startSync", "Sync Account exists");
                    }
                    boolean isEnabled = ContentResolver.getSyncAutomatically(mAccount, context.getResources().getString(R.string.content_authority));
                    if (!isEnabled) {
                        ContentResolver.addPeriodicSync(mAccount, context.getResources().getString(R.string.content_authority), new Bundle(), Constants.SYNC_FREQUENCY);
                    }
                }
            }
        }
        return mAccount;
    }

    public static Account requestSync(Context context) {
        Account[] accounts = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            accounts = AccountManager.get(context).getAccountsByType(context.getResources().getString(R.string.sync_account_type));

            ContentResolver.requestSync(accounts[0], context.getResources().getString(R.string.content_authority), new Bundle());

        }

        return accounts[0];
    }

    /**
     * Create a new account for the sync adapter
     *
     * @param context The application context
     */
    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(Constants.ACCOUNT, context.getResources().getString(R.string.sync_account_type));
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            ContentResolver.setIsSyncable(newAccount, context.getResources().getString(R.string.content_authority), 1);
            ContentResolver.setSyncAutomatically(newAccount, context.getResources().getString(R.string.content_authority), true);
            //Set periodic sync duration in seconds
            ContentResolver.addPeriodicSync(newAccount, context.getResources().getString(R.string.content_authority), new Bundle(), Constants.SYNC_FREQUENCY);
            if (INFO_LOG) {
                Log.i("BDCloudUtils-createSyncAccount", "Sync Account added = " + newAccount);
            }
        } else {
            if (ERROR_LOG) {
                Log.wtf("BDCloudUtils-createSyncAccount", "Error creating sync account");
            }
        }

        return newAccount;
    }

    public static void mayBeCompressImage(File inputPath, File outputPath) throws Exception {

        Exception epriv = null;

        Bitmap bmp = BitmapFactory.decodeFile(inputPath.getPath());

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(outputPath);
            bmp2.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            epriv = e1;
            throw epriv;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            epriv = e;
            throw epriv;
        } finally {
            bmp.recycle();
            bmp2.recycle();
            System.gc();
        }
    }


    public static void sendNotification(@NonNull String messageBody, @NonNull Context context, Class<?> object, @NonNull int notificationId,
                                        String intentName) {

        Log.i("sendNotification", messageBody);
        PendingIntent pendingIntent = null;
        if (object != null) {
            Intent intent = new Intent(context, object);
            intent.putExtra("MyAssignment", "openMyAssignmentFragment");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            pendingIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        } else if (intentName != null && !intentName.equalsIgnoreCase("")) {
            Intent intent = new Intent(intentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            pendingIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        }


//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//         stackBuilder.addNextIntent(intent);
//        PendingIntent pendingIntent = stackBuilder
//                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
//                        | PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.newappicon)
                .setContentTitle("BDCloud")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }


    public static boolean checkNetworkTime(final Context context) {

        boolean retVal = false;
        try {
            if ((Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME) == 0) ||
                    (Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE) == 0)) {
                retVal = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert");
                builder.setMessage("Your time & time zone is not in sync with the network");

                String positiveText = "Enable time sync";
                builder.setPositiveButton(positiveText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent callGPSSettingIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            }
                        });

//        String negativeText = getString(android.R.string.cancel);
//        builder.setNegativeButton(negativeText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // negative button logic
//                    }
//                });


                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();

            } else {
                retVal = true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return retVal;
    }

}
