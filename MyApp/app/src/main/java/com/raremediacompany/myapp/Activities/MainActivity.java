package com.raremediacompany.myapp.Activities;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.BLE.BeaconScannerService;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raremediacompany.myapp.App;
import com.raremediacompany.myapp.Helpers.AlarmReceiver;
import com.raremediacompany.myapp.Holders.CheckedInHolder;
import com.raremediacompany.myapp.R;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;

public class MainActivity extends BaseActivity {
    private Button button;
    private Realm realm;
    private AppCompatTextView userName, messageTV;

    private BDPreferences bdPreferences;
    private String userNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bdPreferences = new BDPreferences(getApplicationContext());
        realm = BDCloudUtils.getRealmBDCloudInstance();

        button = (Button) findViewById(R.id.check_In_button);
        userName = (AppCompatTextView) findViewById(R.id.textView_name);
        messageTV = (AppCompatTextView) findViewById(R.id.textView);
//        MainActivity.this.startService(new Intent(MainActivity.this, BeaconScannerService.class));

        BDCloudUtils.startSync(getApplicationContext());
//        BDCloudUtils.checkNetworkTime(getApplicationContext());
//        scheduleAlarm(getApplicationContext());

//        messageTV.setTypeface(mFontRegular);

        final RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
        Log.i("Logged in UserID", "" + user.getUserId());
        if (user != null) {
            userNameStr = user.getUserName();
            userNameStr = Character.toUpperCase(userNameStr.charAt(0)) + userNameStr.substring(1);
            String[] userNameArr = userNameStr.split(" ");
            if (userNameArr != null) {
                userName.setText("Hi " + userNameArr[0]);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createCheckin(userNameStr);
                Intent intent = new Intent(MainActivity.this, CheckInSuccessfull.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });




        realm = BDCloudUtils.getRealmBDCloudInstance();


    }

    private void createCheckin(String userName) {

        CheckedInHolder checkedInHolder = new CheckedInHolder();
        checkedInHolder.userName = userName;
        checkedInHolder.checkedInTimestamp = new Date().toString();
        checkedInHolder.status = "Checked-In";


        Gson gson = new GsonBuilder().create();
        final String json = gson.toJson(checkedInHolder);

        final RMCUser user = realm.where(RMCUser.class).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a user
                RMCCheckin rmcCheckin = new RMCCheckin();
                rmcCheckin.setLatitude(String.valueOf(bdPreferences.getTransientLatitude()));
                rmcCheckin.setLongitude(String.valueOf(bdPreferences.getTransientLongitude()));
                rmcCheckin.setAccuracy(String.valueOf(bdPreferences.getTransientAccuracy()));
                rmcCheckin.setAltitude(String.valueOf(bdPreferences.getTransientAltitude()));
                rmcCheckin.setCheckinDetails(json);
                rmcCheckin.setAssignmentId(UUID.randomUUID().toString());
                rmcCheckin.setCheckinCategory("Data");
                rmcCheckin.setCheckinType("Data");
                rmcCheckin.setCheckinId(UUID.randomUUID().toString());
                rmcCheckin.setOrganizationId(user .getOrgId());
                rmcCheckin.setTime(new Date());
                realm.copyToRealmOrUpdate(rmcCheckin);
            }
        });

    }


//    public static void scheduleAlarm(Context context) {
//        // Construct an intent that will execute the AlarmReceiver
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        // Create a PendingIntent to be triggered when the alarm goes off
//        final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Setup periodic alarm every every half hour from this point onwards
//        long firstMillis = System.currentTimeMillis(); // alarm is set right away
//        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
//        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,firstMillis,pIntent);
//        }else {
//            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
//                    300000, pIntent);
//        }
//
//
//
//    }

}
