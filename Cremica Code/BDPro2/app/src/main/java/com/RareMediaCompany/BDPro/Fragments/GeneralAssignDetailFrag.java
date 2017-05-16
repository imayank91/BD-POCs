package com.RareMediaCompany.BDPro.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCLocation;
import com.RMC.BDCloud.RealmDB.Model.StatusLog;
import com.RareMediaCompany.BDPro.Activities.GalleryActivity;
import com.RareMediaCompany.BDPro.Activities.MyCustomMapActivity;
import com.RareMediaCompany.BDPro.Activities.MyDetailCustomMapActivity;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontRegular;

/**
 * Created by sidd on 11/29/16.
 */

public class GeneralAssignDetailFrag extends Fragment {

    @BindView(R.id.contact_person_name)
    TextView contactName;

    @BindView(R.id.contact_phoneNum)
    TextView mobile;

    @BindView(R.id.contact_email)
    TextView email;

    @BindView(R.id.assignment_details_instructions)
    TextView instructions;

    @BindView(R.id.instructions_tag)
    TextView instructions_tag;

    @BindView(R.id.startDate)
    TextView start_date;

    @BindView(R.id.endDate)
    TextView end_date;

    @BindView(R.id.start_label)
    TextView start_label;

    @BindView(R.id.end_label)
    TextView end_label;

    @BindView(R.id.address_assignmentDetails)
    TextView location;

    @BindView(R.id.layoutWith_NoMap)
    RelativeLayout layoutWithNoMap;

    @BindView(R.id.contact_person_tag)
    TextView contact_person_tag;

    @BindView(R.id.number_of_images)
    TextView imageCountGallery;

    @BindView(R.id.label_attachments)
    TextView attachmentLabel;

    @BindView(R.id.arrow_forImages)
    ImageView arrow_forImages;

    LocationManager locationManager;
    SupportMapFragment map;
    boolean mapReady = false;
    GoogleApiClient googleApiClient;
    File path;
    View view;
    String[] fileNames;
    String photoPath = "Android/data/com.RMC.BDPro/checkins/"
            + SingleTon.getInstance().getAssignmentModel().assignmentId + "/images";
    Realm realm;
    RMCAssignment rmcAssignment;
    JSONObject object = null;
    List<Marker> markersList;
    LatLngBounds.Builder builder;
    CameraUpdate cu;
    private GoogleMap mGoogleMap;
    private String activeAssgnmentID;
    private int requestCode = 1234;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markersList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_gen_asgndetails, container, false);
        ButterKnife.bind(this, view);
        map = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (path.exists()) {
            fileNames = path.list();
            imageCountGallery.setText(String.valueOf(fileNames.length));
            if((fileNames.length-1)>=0){
                arrow_forImages.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        path = new File(Environment.getExternalStorageDirectory() + "/" + photoPath);

        start_label.setTypeface(mFontRegular);
        end_label.setTypeface(mFontRegular);
        start_date.setTypeface(mFontRegular);
        end_date.setTypeface(mFontRegular);
        contact_person_tag.setTypeface(mFontRegular);
        instructions.setTypeface(mFontRegular);
        instructions_tag.setTypeface(mFontRegular);
        contactName.setTypeface(mFontRegular);
        mobile.setTypeface(mFontRegular);
        email.setTypeface(mFontRegular);
        imageCountGallery.setTypeface(mFontRegular);
        attachmentLabel.setTypeface(mFontRegular);

        contactName.setText(SingleTon.getInstance().getAssignmentModel().contactPersonName);
        mobile.setText(SingleTon.getInstance().getAssignmentModel().mobileNum);
        email.setText(SingleTon.getInstance().getAssignmentModel().email);
        instructions.setText(SingleTon.getInstance().getAssignmentModel().instructions);
        location.setText(SingleTon.getInstance().getAssignmentModel().address);
        start_date.setText(SingleTon.getInstance().getAssignmentModel().startDate);
        end_date.setText(SingleTon.getInstance().getAssignmentModel().endDate);

        activeAssgnmentID = SingleTon.getInstance().getAssignmentModel().assignmentId;
        realm = BDCloudUtils.getRealmBDCloudInstance();

        rmcAssignment = realm.where(RMCAssignment.class).equalTo("assignmentId", activeAssgnmentID).findFirst();
        String assignemntDEtails = rmcAssignment.getAssignmentDetails();

        try {
            object = new JSONObject(assignemntDEtails);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (path.exists()) {
            fileNames = path.list();
            imageCountGallery.setText(String.valueOf(fileNames.length));
        }

    }

    @OnClick(R.id.phone_layout)
    public void call() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please Confirm");
        builder.setMessage("Do you want to call " + SingleTon.getInstance().getAssignmentModel().mobileNum + "? " +
                "Carrier charges may apply. ");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:" + SingleTon.getInstance().getAssignmentModel().mobileNum));
                        startActivity(phoneIntent);

                        maintainAssignmentHistory();

                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });


        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    private void maintainAssignmentHistory() {


        RealmQuery<RMCAssignment> query = realm.where(RMCAssignment.class).equalTo("assignmentId", activeAssgnmentID);
        final RealmResults<RMCAssignment> result = query.findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                StatusLog statusLog = realm.createObject(StatusLog.class);
                statusLog.setPhotoUri("");
                SimpleDateFormat myformatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                statusLog.setTimestamp(myformatter.format(new Date()));
                statusLog.setType("Call");
                result.get(0).getStatusLog().add(statusLog);
                realm.copyToRealmOrUpdate(result);
            }
        });

    }

    @OnClick(R.id.email_layout)
    public void email() {

        String subject = "";
        String bodyText = "";

        String mailto = "mailto:" + SingleTon.getInstance().getAssignmentModel().email +
                "?cc=" + "" +
                "&subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(bodyText);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        startActivity(emailIntent);

    }

    @OnClick(R.id.location_layout)
    public void location() {

        RMCLocation location = rmcAssignment.getLocation();
     //   Log.i("Location : ", " Lat : " + location.getLatitude() + " Lng " + location.getLongitude() + "?z=18");

        Uri inUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + rmcAssignment.getAddress());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, inUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

//        Intent mapIntent = new Intent(getActivity(), MyDetailCustomMapActivity.class);
//        mapIntent.putExtra(location.getLatitude(),"Latitude");
//        mapIntent.putExtra(location.getLongitude(),"Longitude");
//        startActivityForResult(mapIntent, requestCode);
    }

    @OnClick(R.id.gallery_button)
    public void openGallery() {

        ArrayList<String> images = new ArrayList<String>();

        if (path.exists()) {
            fileNames = path.list();

            for (int i = 0; i < fileNames.length; i++) {

                Log.i("My gallery image name ", "" + fileNames[i]);
                images.add(path.getPath() + "/" + fileNames[i]);
            }
        }

        //  ArrayList<String> images = new ArrayList<String>();
        //  images.add("http://sourcey.com/images/stock/salvador-dali-metamorphosis-of-narcissus.jpg");
        //  images.add("http://sourcey.com/images/stock/salvador-dali-the-dream.jpg");
        //  images.add("http://sourcey.com/images/stock/salvador-dali-persistence-of-memory.jpg");
        //  images.add("http://sourcey.com/images/stock/simpsons-persistence-of-memory.jpg");
        //  images.add("http://sourcey.com/images/stock/salvador-dali-the-great-masturbator.jpg");

        Log.i("IMAGE SIZE", String.valueOf(images.size()));

        if ((images.size() != 0)) {

            Intent intent = new Intent(getActivity(), GalleryActivity.class);
            intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, images);
            startActivity(intent);
        } else {
            Snackbar.make(view, "No images to display", Snackbar.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("LongLogTag")
    private void setupMarkers() {

        // for (RMCAssignment ass : result) {


//            builder = new LatLngBounds.Builder();
//
//            for (Marker m : markersList) {
//                builder.include(m.getPosition());
//            }
//
//            int padding = 30;
//            /**create the bounds from latlngBuilder to set into map camera*/
//            LatLngBounds bounds = builder.build();
//            /**create the camera with bounds and padding to set into map*/
//            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

    }

    protected synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

}

