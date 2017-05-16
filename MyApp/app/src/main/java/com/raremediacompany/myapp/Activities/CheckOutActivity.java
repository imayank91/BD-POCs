package com.raremediacompany.myapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raremediacompany.myapp.Helpers.PreferenceforApp;
import com.raremediacompany.myapp.Holders.CheckedInHolder;
import com.raremediacompany.myapp.R;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

public class CheckOutActivity extends BaseActivity {

    private Button checkOutBtn;
    private PreferenceforApp preferenceforApp;
    private BDPreferences bdPreferences;
    private Realm realm;
    private String userNameStr;
    private TextView timeStampText, userNameTV, textView_scan2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        realm = BDCloudUtils.getRealmBDCloudInstance();
        bdPreferences = new BDPreferences(getApplicationContext());
        preferenceforApp = new PreferenceforApp(CheckOutActivity.this);

        try {
            final RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();

            if (user != null) {
                userNameStr = user.getUserName();

            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }


        checkOutBtn = (Button) findViewById(R.id.check_out_button);
        timeStampText = (TextView) findViewById(R.id.timestampText);
        timeStampText.setTypeface(mFontRegular);
        userNameTV = (TextView) findViewById(R.id.textView_scan);
        //userNameTV.setTypeface(mFontRegular);
        textView_scan2 = (TextView) findViewById(R.id.textView_scan2);
        //textView_scan2.setTypeface(mFontRegular);
        userNameStr = Character.toUpperCase(userNameStr.charAt(0)) + userNameStr.substring(1);

        String[] userName = userNameStr.split(" ");

        userNameTV.setText("Hi " + userName[0] + ", How's it ");

        timeStampText.setText("Your last check in: " + preferenceforApp.getCheckedTimestamp());

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCheckin(userNameStr);
                preferenceforApp.setIsCheckedIn(false);
                Intent intent = new Intent(CheckOutActivity.this, MainActivity.class);
                startActivity(intent);
                CheckOutActivity.this.finish();
            }
        });
    }

    private void createCheckin(String userName) {

        CheckedInHolder checkedInHolder = new CheckedInHolder();
        checkedInHolder.userName = userName;
        checkedInHolder.checkedInTimestamp = new Date().toString();
        checkedInHolder.status = "Checked-Out";


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
                rmcCheckin.setOrganizationId(user.getOrgId());
                rmcCheckin.setTime(new Date());
                realm.copyToRealmOrUpdate(rmcCheckin);
            }
        });

    }
}
