package com.RareMediaCompany.BDPro.Activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RMC.BDCloud.RealmDB.Model.StatusLog;
import com.RareMediaCompany.BDPro.DataModels.AssignmentModel;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sidd on 12/19/16.
 */

public class NotesActivity extends BaseActivity {


    @BindView(R.id.makeNote)
    EditText note;

    @BindView(R.id.save_notes_button)
    Button save;

    @BindView(R.id.edit_notes_button)
    Button edit;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    AssignmentModel assignmentModel;
    Realm realm;
    JSONObject object = null;
    RMCAssignment assignment = null;
    String activeAssgnmentID = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        ButterKnife.bind(this);
        initToolbar();
        note.setTypeface(mFontRegular);
        save.setTypeface(mFontRegular);
        edit.setTypeface(mFontRegular);

        assignmentModel = new AssignmentModel();
        activeAssgnmentID = SingleTon.getInstance().getAssignmentModel().assignmentId;
        realm = BDCloudUtils.getRealmBDCloudInstance();

        assignment = realm.where(RMCAssignment.class).equalTo("assignmentId", activeAssgnmentID).findFirst();
        String assignemntDEtails = assignment.getAssignmentDetails();

        try {
            object = new JSONObject(assignemntDEtails);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Notes ", "" + SingleTon.getInstance().getAssignmentModel().notes);
        note.setText(SingleTon.getInstance().getAssignmentModel().notes);
//        note.setEnabled(false);
        note.setTextColor(Color.BLACK);

        if (assignment.getStatus().contentEquals("Completed")) {

            edit.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            note.setEnabled(false);




        }


    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @OnClick(R.id.edit_notes_button)
    public void editNotes() {
        Log.i("Editing", "HERE");
        edit.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        note.setEnabled(true);
        note.setText(SingleTon.getInstance().getAssignmentModel().notes);

    }


    @OnClick(R.id.scrollView_layout)
    public void editHereNotes() {
        //  note.setEnabled(true);
        //  Toast.makeText(NotesActivity.this, "Hey man", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.save_notes_button)
    public void saveNotes() {

        Log.i("Saving", "HERE");
        save.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);


        if (!object.has("notes")) {
            Log.i("CREATED NOTES", "IN REALM");
            try {
                object.put("notes", note.getText().toString());
                Log.i("ENTERING NOTES ", "IN REALM");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                object.put("notes", note.getText().toString());
                Log.i("ENTERING NOTES ", "IN REALM");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AssignmentModel model = SingleTon.getInstance().getAssignmentModel();
        model.notes = note.getText().toString();

        Log.i("MODEL ", "NOTES " + model.notes);

        SingleTon.getInstance().setAssignmentModel(model);

        final JSONObject finalObject = object;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                assignment.setAssignmentDetails(finalObject.toString());
                Log.i("FINAL OBJECT", " " + finalObject);
                realm.copyToRealmOrUpdate(assignment);
            }
        });


        final RMCUser user = realm.where(RMCUser.class).findFirst();
        final BDPreferences prefs = new BDPreferences(getApplicationContext());
        final String assignmentId = SingleTon.getInstance().getAssignmentModel().assignmentId;
        String uuid = UUID.randomUUID().toString();

        final JSONObject object = new JSONObject();
        try {
            object.put("userNotes", note.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a checkin
                if (user != null) {
                    RMCCheckin rmcCheckin = new RMCCheckin();
                    rmcCheckin.setLatitude(String.valueOf(prefs.getTransientLatitude()));
                    rmcCheckin.setLongitude(String.valueOf(prefs.getTransientLongitude()));
                    rmcCheckin.setAccuracy(String.valueOf(prefs.getTransientAccuracy()));
                    rmcCheckin.setAltitude(String.valueOf(prefs.getTransientAltitude()));
                    rmcCheckin.setCheckinDetails(object.toString());
                    Log.i("ASSIGNMENT ID", assignmentId);
                    rmcCheckin.setAssignmentId(assignmentId);
                    rmcCheckin.setCheckinCategory("Non-Transient");
                    rmcCheckin.setCheckinType("In-Progress");
                    String checkinId = UUID.randomUUID().toString();
                    Log.i("photo checkin id =", checkinId);
                    rmcCheckin.setCheckinId(checkinId);
                    rmcCheckin.setOrganizationId(user.getOrgId());
                    rmcCheckin.setTime(new Date());
                    realm.copyToRealmOrUpdate(rmcCheckin);
                }
            }
        });


        maintainAssignmentHistory();

        realm.close();

        note.setEnabled(false);
        note.setTextColor(Color.BLACK);
    }

    private void maintainAssignmentHistory() {

        RealmQuery<RMCAssignment> query = realm.where(RMCAssignment.class).equalTo("assignmentId", activeAssgnmentID);
        final RealmResults<RMCAssignment> result = query.findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if (assignment.getStatus().contentEquals("Assigned") || assignment.getStatus().contentEquals("Created")) {
                    result.get(0).setStatus("In Progress");
                }
                StatusLog statusLog = realm.createObject(StatusLog.class);
                statusLog.setPhotoUri("");
                SimpleDateFormat myformatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                statusLog.setTimestamp(myformatter.format(new Date()));
                statusLog.setType("Note");
                result.get(0).getStatusLog().add(statusLog);
                realm.copyToRealmOrUpdate(result);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @OnClick(R.id.back_button_notes)
    public void closeActivity() {
        this.finish();
    }
}
