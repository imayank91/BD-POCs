package com.RareMediaCompany.BDPro.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCCheckin;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RMC.BDCloud.RealmDB.Model.StatusLog;
import com.RareMediaCompany.BDPro.Adapter.MyFragmentPagerAdapter;
import com.RareMediaCompany.BDPro.Fragments.GeneralAssignDetailFrag;
import com.RareMediaCompany.BDPro.Fragments.HistoryAssignDetailFrag;
import com.RareMediaCompany.BDPro.Fragments.SignatureFragment;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontRegular;

/**
 * Created by sidd on 11/29/16.
 */

public class AssignmentDetailActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1034;
    public static File photo;
    public static boolean submitted = false;
    public static boolean movedToInProgress = false;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.submit_assignment_button)
    Button submitButton;
    Uri photoUri;
    String photoPath = "Android/data/com.RMC.BDPro/checkins/"
            + SingleTon.getInstance().getAssignmentModel().assignmentId + "/images";
    Realm realm;
    RMCAssignment rmcAssignment;
    JSONObject object = null;
    View parentLayout;
    EditText userInputImageName;
    private MyFragmentPagerAdapter fragmentAdapter;
    private File file;
    private String statusLogJson = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignmentdetails);
        parentLayout = findViewById(R.id.container_acitvityassignment);
        ButterKnife.bind(this);
        initToolbar();
        initView();

        String activeAssgnmentID = SingleTon.getInstance().getAssignmentModel().assignmentId;
        realm = BDCloudUtils.getRealmBDCloudInstance();

        rmcAssignment = realm.where(RMCAssignment.class).equalTo("assignmentId", activeAssgnmentID).findFirst();
        String assignemntDEtails = rmcAssignment.getAssignmentDetails();
        toolbarTitle.setText(SingleTon.getInstance().getAssignmentModel().jobNumber);

        try {
            object = new JSONObject(assignemntDEtails);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (rmcAssignment.getStatus().contentEquals("Completed")) {
            submitButton.setVisibility(View.GONE);
        }



        if(!rmcAssignment.isOpened()){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    rmcAssignment.setOpened(true);
                    realm.copyToRealmOrUpdate(rmcAssignment);
                }
            });
        }

    }

    private void initToolbar() {
        //    toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initView() {

        //  toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(mFontRegular);
        setupViewPager(viewPager);

        //  tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("General");
        tabLayout.getTabAt(1).setText("History");
        submitButton.setTypeface(mFontRegular);
    }

    private void setupViewPager(ViewPager viewpager) {

        fragmentAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        fragmentAdapter.addFragment(new GeneralAssignDetailFrag(), "General");
        fragmentAdapter.addFragment(new HistoryAssignDetailFrag(), "History");
        viewpager.setAdapter(fragmentAdapter);
    }

    @OnClick(R.id.signatureButton)
    public void clickedSignatureButton() {

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_acitvityassignment, new SignatureFragment())
                .addToBackStack(null)
                .commit();

    }


    @OnClick(R.id.notes_button)
    public void clickedNotesButton() {

        startActivity(new Intent(AssignmentDetailActivity.this, NotesActivity.class));


    }

    @OnClick(R.id.camera_button)
    public void imageCapture() {


        if (rmcAssignment.getStatus().contentEquals("Completed")) {
            Snackbar.make(parentLayout, "You have Completed The Assignment", Snackbar.LENGTH_SHORT).show();
        } else {
            takeImageTask();
        }

    }

    private void takeImageTask() {

        file = new File(Environment.getExternalStorageDirectory() + "/" + photoPath);

        if (!file.exists()) {
            file.mkdirs();

            UUID checkinUUID = UUID.randomUUID();
            photo = new File(file, checkinUUID + ".jpg");

            if (Build.VERSION.SDK_INT >= 7.0) {
                photoUri = FileProvider.getUriForFile(AssignmentDetailActivity.this,
                        getApplicationContext().getPackageName() + ".provider", photo);
            } else {
                photoUri = Uri.fromFile(photo);
            }


            Log.i("Directory created", "Here");

            try {

                object.put("photoPath", photoPath);
                Log.i("SAVED  " + photoUri, " IN REALM");
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

            dispatchTakePictureIntent();
        } else {
            UUID checkinUUID = UUID.randomUUID();
            photo = new File(file, checkinUUID + ".jpg");

        /*create file to save image*/
            if (Build.VERSION.SDK_INT >= 7.0) {
                photoUri = FileProvider.getUriForFile(AssignmentDetailActivity.this,
                        getApplicationContext().getPackageName() + ".provider", photo);
            } else {
                photoUri = Uri.fromFile(photo);
            }

            Log.i("Directory present", "Here");
            dispatchTakePictureIntent();
        }


    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }


        maintainAssignmentHistory();

//        if (rmcAssignment.getStatus().contentEquals("Assigned") || rmcAssignment.getStatus().contentEquals("Created")) {
//            moveAssignmentToProgress();
//        }
    }

    private void maintainAssignmentHistory() {

        String assignmentId = SingleTon.getInstance().getAssignmentModel().assignmentId;

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RealmQuery<RMCAssignment> query = realm.where(RMCAssignment.class).equalTo("assignmentId", assignmentId);
        final RealmResults<RMCAssignment> result = query.findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if (rmcAssignment.getStatus().contentEquals("Assigned") || rmcAssignment.getStatus().contentEquals("Created")||rmcAssignment.getStatus().contentEquals("Downloaded") ) {
                    result.get(0).setStatus("In Progress");
                }
                StatusLog statusLog = realm.createObject(StatusLog.class);
                statusLog.setPhotoUri("");
                SimpleDateFormat myformatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                statusLog.setTimestamp(myformatter.format(new Date()));
                statusLog.setType("Photo");
                result.get(0).getStatusLog().add(statusLog);
                realm.copyToRealmOrUpdate(result);
            }
        });

        realm.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            movedToInProgress = true;

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        BDCloudUtils.mayBeCompressImage(photo, photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(AssignmentDetailActivity.this);

            View mView = layoutInflaterAndroid.inflate(R.layout.imagename_input_dialog_box, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(AssignmentDetailActivity.this);
            alertDialogBuilderUserInput.setView(mView);

            userInputImageName = (EditText) mView.findViewById(R.id.userInputDialog);
            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            // ToDo get user input here

                            Log.i("Hey ", " Send " + id);
                            uploadImageChecking(id);
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    //
                                    Log.i("Hey ", " Cancel " + id);
                                    uploadImageChecking(id);
                                    dialogBox.cancel();
                                }
                            });
            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
        }
    }

    private void uploadImageChecking(int id) {

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        final RMCUser user = realm.where(RMCUser.class).findFirst();
        final BDPreferences prefs = new BDPreferences(getApplicationContext());
        final String assignmentId = SingleTon.getInstance().getAssignmentModel().assignmentId;
        String uuid = UUID.randomUUID().toString();
        final String imageName = uuid + "_" + assignmentId + "_" + "Photo.jpg";
        String image_Name = null;

        if (id == -1) {
            image_Name = userInputImageName.getText().toString();
        }

        final String finalImage_Name = image_Name;
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
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("imageName", finalImage_Name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    rmcCheckin.setCheckinDetails(jsonObject.toString());
                    Log.i("ASSIGNMENT ID", assignmentId);
                    rmcCheckin.setAssignmentId(assignmentId);
                    rmcCheckin.setCheckinCategory("Non-Transient");
                    rmcCheckin.setCheckinType("Photo");
                    String checkinId = UUID.randomUUID().toString();
                    Log.i("photo checkin id =", checkinId);
                    rmcCheckin.setCheckinId(checkinId);
                    rmcCheckin.setOrganizationId(user.getOrgId());
                    rmcCheckin.setTime(new Date());
                    rmcCheckin.setImageUri(photo.toString());
                    rmcCheckin.setImageName(imageName);
                    realm.copyToRealmOrUpdate(rmcCheckin);
                }
            }
        });
        realm.close();
    }

    @OnClick(R.id.submit_assignment_button)
    public void submitAssignment() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Confirm");
        builder.setMessage("Do you want to submit this Assignment ?");

        String positiveText = getString(android.R.string.yes);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String assignmentId = SingleTon.getInstance().getAssignmentModel().assignmentId;

                        Log.i(" ASSIGNMENT ID ", "" + assignmentId);

                        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
                        RealmQuery<RMCAssignment> query = realm.where(RMCAssignment.class).equalTo("assignmentId", assignmentId);
                        final RealmResults<RMCAssignment> result = query.findAll();

                        Log.i("Result Size ", "is " + result.size());
                        if (result.size() > 0) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    result.get(0).setStatus("Completed");
                                    realm.copyToRealmOrUpdate(result);
                                }
                            });
                        }


                        final RMCUser user = realm.where(RMCUser.class).findFirst();
                        final BDPreferences prefs = new BDPreferences(getApplicationContext());
                        String uuid = UUID.randomUUID().toString();
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
                                    rmcCheckin.setCheckinType("Submitted");
                                    String checkinId = UUID.randomUUID().toString();
                                    Log.i("photo checkin id =", checkinId);
                                    rmcCheckin.setCheckinId(checkinId);
                                    rmcCheckin.setOrganizationId(user.getOrgId());
                                    rmcCheckin.setTime(new Date());
                                    realm.copyToRealmOrUpdate(rmcCheckin);
                                }
                            }
                        });

                        realm.close();
                        submitted = true;
                        AssignmentDetailActivity.movedToInProgress = false;
                        finish();
                    }
                });


        String negativeText = getString(android.R.string.no);
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

    @OnClick(R.id.back_button_assgn_details)
    public void back() {
        this.finish();
    }
}
