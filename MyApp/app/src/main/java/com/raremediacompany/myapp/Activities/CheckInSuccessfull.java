package com.raremediacompany.myapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.raremediacompany.myapp.Helpers.PreferenceforApp;
import com.raremediacompany.myapp.R;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dhananjay on 4/17/2017.
 */

public class CheckInSuccessfull extends AppCompatActivity {
    private ImageView imageView;
    private PreferenceforApp preferenceforApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_succssfull_acitvity);

        preferenceforApp = new PreferenceforApp(CheckInSuccessfull.this);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(CheckInSuccessfull.this, CheckOutActivity.class);
                // startActivity(intent);
            }
        });

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {

                    public void run() {
                        // TODO Auto-generated method stub

                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                        long millis = cal.getTimeInMillis();
                        Date currentLocalTime = cal.getTime();
                        DateFormat date = new SimpleDateFormat("HH:mm");
                        // you can get seconds by adding  "...:ss" to it
                        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                        String localTime = date.format(currentLocalTime);

                        Log.i("local time", "" + localTime);


                        Intent intent = new Intent(CheckInSuccessfull.this, CheckOutActivity.class);
                        startActivity(intent);
                        preferenceforApp.setCheckedInMillis(millis);
                        preferenceforApp.setIsCheckedIn(true);
                        preferenceforApp.setCheckedInTimestamp(localTime);
                        timer.cancel();
                        CheckInSuccessfull.this.finish();

                    }

                });
            }
        }, 1000, 1000);


    }
}
