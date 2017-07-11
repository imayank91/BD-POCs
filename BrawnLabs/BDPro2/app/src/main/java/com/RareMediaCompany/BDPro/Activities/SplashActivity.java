package com.RareMediaCompany.BDPro.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RareMediaCompany.BDPro.Helpers.PreferenceforApp;
import com.RareMediaCompany.BDPro.R;

import org.json.JSONObject;

/**
 * Created by mayanksaini on 16/12/16.
 */

public class SplashActivity extends AppCompatActivity implements RequestCallback {

    private final int SPLASH_DISPLAY_LENGTH = 1200;
    private PreferenceforApp prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefs = new PreferenceforApp(getApplicationContext());

        if (prefs.getUserActivated() == false) {
            startIntent(Login.class);
        } else {
            BDCloudUtils.startSync(SplashActivity.this);
            startIntent(MyDashboardActivity.class);
        }
    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

    }

    private void startIntent(final Class<?> activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, activity);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
