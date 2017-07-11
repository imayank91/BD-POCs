package com.RMC.BDCloud.Firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.RMC.BDCloud.Android.BDCloudPlatformServiceAndroid;
import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.DownloadAssignmentTask;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import static com.RMC.BDCloud.Android.BDCloudUtils.DEBUG_LOG;
import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by mayanksaini on 09/11/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements RequestCallback {

    private static final String TAG = "MyFirebaseMessagingService";
    private RequestCallback callback;
    private String assignmentId, notificationBody;

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (INFO_LOG) {
            Log.i(TAG, "From: " + remoteMessage.getFrom());
//            Log.i(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
            Log.i(TAG, "Notification assignmentId: " + remoteMessage.getData().get("assignmentId"));
            Log.i(TAG, "Notification notificationType: " + remoteMessage.getData().get("notificationType"));
        }

        assignmentId = remoteMessage.getData().get("assignmentId");

        if (remoteMessage.getNotification() != null) {
            notificationBody = remoteMessage.getNotification().getBody();
        } else if (assignmentId != null) {
            notificationBody = "New Assignment has been assigned to you";
        } else {
            notificationBody = "Welcome to Blue Dolphin Cloud";
        }

        Log.d("Firebase Messaging Service 0", "assignmentId" + assignmentId);
        if (assignmentId != null && !assignmentId.equalsIgnoreCase("")) {
            if (DEBUG_LOG) {
                Log.d(TAG, "Assignment Id- " + assignmentId);
            }
            DownloadAssignmentTask task = new DownloadAssignmentTask(getApplicationContext(), this, assignmentId, notificationBody);
            task.downloadAssignment();
        } else {
            if (DEBUG_LOG) {
                Log.d(TAG, "Simple Notification- " + notificationBody);
            }
            onResponseReceived(null, true, notificationBody, Constants.downloadAssignmentsCallback);
        }

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.downloadAssignmentsCallback) {
            if (status == true) {
                callback = BDCloudUtils.initBaseAppClass(null);
                if (callback != null) {
                    callback.onResponseReceived(null, true, message, Constants.pushNotificationCallback);
                }
            }
        }
    }
}
