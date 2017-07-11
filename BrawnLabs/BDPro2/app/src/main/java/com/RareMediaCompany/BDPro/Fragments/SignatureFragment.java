package com.RareMediaCompany.BDPro.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RMC.BDCloud.RealmDB.Model.StatusLog;
import com.RareMediaCompany.BDPro.Activities.SignatureBaseClass;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontRegular;

/**
 * Created by sidd on 12/12/16.
 */

public class SignatureFragment extends Fragment {

    public String current = null;
    View rootView;
    View myView;
    View mview;
    String signature_photo_path = "Android/data/com.RMC.BDPro/checkins/"
            + SingleTon.getInstance().getAssignmentModel().assignmentId + "/signature";

    File signature_photo;

    @BindView(R.id.cleartextview)
    TextView cleartext;
    @BindView(R.id.relative_signature_ids)
    RelativeLayout relative_layout;
    @BindView(R.id.relative_signature_id1)
    RelativeLayout relative_layout_1;
    @BindView(R.id.fullscreen_imageView)
    ImageView imageview1;
    @BindView(R.id.clearLayout)
    RelativeLayout clearLayout;
    @BindView(R.id.bottom_ll)
    LinearLayout signautre_linear_layout;
    @BindView(R.id.signature_linearLayout)
    LinearLayout mContent;

    @BindView(R.id.save_signature)
    Button save_signature_button;
    @BindView(R.id.cancel_and_goback)
    Button canel_button;
    @BindView(R.id.signature_label)
    TextView signature_label;
    private JSONObject object = null;
    private Realm realm;
    private RMCAssignment rmcAssignment;
    private TextView jobNumber;
    private AppCompatTextView addressText, appointmentTime;
    private SignatureBaseClass mSignature;
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private EditText notesEdittext;
    private LinearLayout saveLayout, cancelLayout;
    private String activeAssgnmentID = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_signature, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ButterKnife.bind(this, rootView);

        this.myView = rootView;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mSignature = new SignatureBaseClass(getActivity(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mview = mContent;

        return rootView;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        signature_photo = new File(Environment.getExternalStorageDirectory() + "/" + signature_photo_path);
        save_signature_button.setTypeface(mFontRegular);
        signature_label.setTypeface(mFontRegular);
        canel_button.setTypeface(mFontRegular);

         activeAssgnmentID = SingleTon.getInstance().getAssignmentModel().assignmentId;
        realm = BDCloudUtils.getRealmBDCloudInstance();

        rmcAssignment = realm.where(RMCAssignment.class).equalTo("assignmentId", activeAssgnmentID).findFirst();
        String assignemntDEtails = rmcAssignment.getAssignmentDetails();

        if (rmcAssignment.getStatus().contentEquals("Completed")) {
            save_signature_button.setVisibility(View.GONE);
            relative_layout_1.setVisibility(View.VISIBLE);
            relative_layout.setVisibility(View.GONE);

        }

        try {
            object = new JSONObject(assignemntDEtails);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!signature_photo.exists()) {
            signature_photo.mkdirs();

            String signName = UUID.randomUUID() + "_Signature.jpg";
            signature_photo = new File(signature_photo, signName);

        } else {
            if (object.has("signUri")) {

                try {
                    String signUri = object.getString("signUri");
                    Glide.with(SignatureFragment.this).load(signUri).asBitmap().into(imageview1);
                    relative_layout_1.setVisibility(View.VISIBLE);
                    relative_layout.setVisibility(View.GONE);
                    save_signature_button.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                String signName = UUID.randomUUID() + "_Signature.jpg";
                signature_photo = new File(signature_photo, signName);
            }
            Log.i("Signature ", "SAVED" + signature_photo_path);
            Log.i("Signature ", "SAVED" + signature_photo.getPath());

        }
    }

    @OnClick(R.id.save_signature)
    public void saveSignature() {

        mContent.setDrawingCacheEnabled(true);
        //  Log.i("signature_photo.getPath()",signature_photo.getPath());
        mSignature.save(mContent, signature_photo);

        Log.i("CREATED NOTES", "IN REALM");
        try {
            object.put("signUri", signature_photo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject finalObject = object;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                rmcAssignment.setAssignmentDetails(finalObject.toString());
                realm.copyToRealmOrUpdate(rmcAssignment);
            }
        });

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        final RMCUser user = realm.where(RMCUser.class).findFirst();
        final BDPreferences prefs = new BDPreferences(getActivity());
        final String assignmentId = SingleTon.getInstance().getAssignmentModel().assignmentId;
        String uuid = UUID.randomUUID().toString();
        final String imageName = uuid + "_" + assignmentId + "_" + "Signature.jpg";

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
                    rmcCheckin.setCheckinDetails(new JSONObject().toString());
                    Log.i("ASSIGNMENT ID", assignmentId);
                    rmcCheckin.setAssignmentId(assignmentId);
                    rmcCheckin.setCheckinCategory("Non-Transient");
                    rmcCheckin.setCheckinType("Photo");
                    String checkinId = UUID.randomUUID().toString();
                    Log.i("photo checkin id =", checkinId);
                    rmcCheckin.setCheckinId(checkinId);
                    rmcCheckin.setOrganizationId(user.getOrgId());
                    rmcCheckin.setTime(new Date());
                    rmcCheckin.setImageUri(signature_photo.toString());
                    rmcCheckin.setImageName(imageName);
                    realm.copyToRealmOrUpdate(rmcCheckin);
                }
            }
        });

        maintainAssignmentHistory();


        realm.close();

        getActivity().onBackPressed();

    }

    public void maintainAssignmentHistory(){

        RealmQuery<RMCAssignment> query = realm.where(RMCAssignment.class).equalTo("assignmentId", activeAssgnmentID);
        final RealmResults<RMCAssignment> result = query.findAll();


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if (rmcAssignment.getStatus().contentEquals("Assigned") || rmcAssignment.getStatus().contentEquals("Created")) {
                    result.get(0).setStatus("In Progress");
                }
                StatusLog statusLog = realm.createObject(StatusLog.class);
                statusLog.setPhotoUri("");
                SimpleDateFormat myformatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                statusLog.setTimestamp(myformatter.format(new Date()));
                statusLog.setType("Signature");
                result.get(0).getStatusLog().add(statusLog);
                realm.copyToRealmOrUpdate(result);
            }
        });

    }


    @OnClick(R.id.clearLayout)
    public void clearLayout() {
        Log.v("log_tag", "Panel Cleared");
        mSignature.clear();
    }

    @OnClick(R.id.cancel_and_goback)
    public void Back() {
        Log.i("SIGNATURE ", "FRAGMENT " + mContent);
        getActivity().onBackPressed();
    }

}
