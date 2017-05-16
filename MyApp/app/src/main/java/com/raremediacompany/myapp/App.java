package com.raremediacompany.myapp;

import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.RequestCallback;
import com.raremediacompany.myapp.Activities.MainActivity;

import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by amresh on 4/19/17.
 */

public class App extends MultiDexApplication implements RequestCallback {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

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
}
