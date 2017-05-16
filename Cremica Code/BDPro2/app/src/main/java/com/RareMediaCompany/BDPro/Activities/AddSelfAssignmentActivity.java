package com.RareMediaCompany.BDPro.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.Android.CreateAssignmentTask;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.Holders.AssignmentDataHolder;
import com.RMC.BDCloud.Holders.AssignmentHolder;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignmentDraft;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RareMediaCompany.BDPro.DataModels.SelfAssignmentModel;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.Helpers.TrackGPS;
import com.RareMediaCompany.BDPro.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sidd on 11/29/16.
 */

public class AddSelfAssignmentActivity extends BaseActivity implements RequestCallback {

    public static boolean assignmentCreated = false;
    @BindView(R.id.label_name)
    TextView labelName;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.label_address)
    TextView labelAddress;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.label_contact_person)
    TextView labelContactPerson;
    @BindView(R.id.contact)
    EditText person;
    @BindView(R.id.label_contact)
    TextView labelContact;
    @BindView(R.id.contactNum)
    EditText contactNum;
    @BindView(R.id.label_email)
    TextView labelEmail;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.label_startDate)
    TextView label_startDate;
    @BindView(R.id.startDate)
    TextView startDate;
    @BindView(R.id.label_endDate)
    TextView label_endDate;
    @BindView(R.id.endDate)
    TextView endDate;
    //    @BindView(R.id.label_recurrence)
//    TextView label_Reccurence;
//    @BindView(R.id.label_reminders)
//    TextView label_Reminders;
    @BindView(R.id.cancel_create_assignment)
    Button cancel_button;
    @BindView(R.id.save_create_assignment)
    Button save_assignment;
    RMCUser result;
    String assignmentDeadlineUTC = null; //"Sat Dec 21 2016 11:37:19 GMT+0530 (IST)";
    String assignmentStartTImeUTC = null; //"Tue Dec 20 2016 23:37:19 GMT+0530 (IST)";
    String addedOnUTC, updatedOnUTC;
    Time time;
    int year1, month1, date, hours, min;
    String dateAsString;
    RMCUser rmcUser;
    private Realm realm;
    private Calendar calendar, calendar2, dateAndTimeCalender;
    private String str;
    private Boolean is24mode = true;
    private TrackGPS gps;
    private int request_Code = 200;
    private String mylat;
    private String mylong;
    private String streetAddress;
    private Date assignmentDeadlineInDateFormat, startTimeInDateFormat;
    EditText assignmentDraftDesc;
    @BindView(R.id.saveButton)
    Button bottom_save_button;
    @BindView(R.id.saveAsDraftButton)
    Button bottom_saveAsDrafts_button;
    RealmResults<RMCAssignmentDraft> draftsResults;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addselfassignment);

        ButterKnife.bind(this);
        settingTypeFaces();
        realm = BDCloudUtils.getRealmBDCloudInstance();
        calendar = Calendar.getInstance();
        calendar2 = Calendar.getInstance();

        Date myDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(myDate);
        Date time = calendar.getTime();
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        outputFmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        dateAsString = outputFmt.format(time);
        System.out.println(dateAsString);

        save_assignment.setTypeface(mFontRegular);
        cancel_button.setTypeface(mFontRegular);
        RealmQuery<RMCUser> query = realm.where(RMCUser.class);
        rmcUser = query.findFirst();
        name.setText(rmcUser.getUserName());

        if(isNetworkConnected()){
            address.setFocusable(false);

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivityForResult(new Intent(AddSelfAssignmentActivity.this, MyCustomMapActivity.class), request_Code);
            }
        });
        }else {
            address.setFocusable(true);
            address.setHint("Enter Address (we'll take current location)");
            Toast.makeText(AddSelfAssignmentActivity.this, "No Internet Connection, Enter address", Toast.LENGTH_LONG).show();

        }

//
//        Bundle bundle = this.getIntent().getExtras();
//
//        if(bundle.getSerializable("Drafts")!=null){
//            draftsResults = (Rea)
//        }

    }


    @Override
    protected void onResume() {
        super.onResume();

//        if(isNetworkConnected()){
//            address.setFocusable(false);
//
//            address.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivityForResult(new Intent(AddSelfAssignmentActivity.this, MyCustomMapActivity.class), request_Code);
//                }
//            });
//        }else {
//
//            address.setFocusable(true);
//            address.setHint("Enter Address (we'll take current location)");
//            Toast.makeText(AddSelfAssignmentActivity.this, "No Internet Connection, Enter address", Toast.LENGTH_LONG).show();
//
//        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {

                address.setText(data.getStringExtra("addressInString"));
                mylat = data.getStringExtra("lat");
                mylong = data.getStringExtra("long");

            }
        }


    }

    @OnClick(R.id.saveButton)
    public void saveAssignment() {

        AssignmentDataHolder dataHolder = new AssignmentDataHolder();
        BDPreferences prefs = new BDPreferences(getApplicationContext());

        RealmQuery<RMCUser> query = realm.where(RMCUser.class);
        result = query.findFirst();

        dataHolder.organizationId = result.getOrgId();
        dataHolder.status = "Created";
        ArrayList<String> assIDS = new ArrayList<>();
        assIDS.add(result.getUserId());
        dataHolder.assigneeIds = assIDS;
        dataHolder.assignmentDeadline = assignmentDeadlineUTC;
        dataHolder.time = dateAsString;
        dataHolder.addedOn = addedOnUTC;
        dataHolder.updatedOn = updatedOnUTC;

        if(mylat == null || mylong ==null){
            gps = new TrackGPS(AddSelfAssignmentActivity.this);

            if(gps.canGetLocation()) {

            dataHolder.latitude = String.valueOf(gps.getLatitude());
            dataHolder.longitude = String.valueOf(gps.getLongitude());
             }else {
            gps.showSettingsAlert();
            }
        }
        else
        {
            dataHolder.latitude = mylat;
            dataHolder.longitude = mylong;
        }

        dataHolder.altitude = String.valueOf(prefs.getTransientAltitude());
        dataHolder.accuracy = String.valueOf(prefs.getTransientAccuracy());
        dataHolder.assignmentAddress = address.getText().toString();
        dataHolder.assignmentStartTime = assignmentStartTImeUTC;
        dataHolder.assignmentId = UUID.randomUUID().toString();

        SelfAssignmentModel model = new SelfAssignmentModel();
        model.mobile = contactNum.getText().toString();
        model.email = email.getText().toString();
        model.contactPerson = person.getText().toString();
        model.name = name.getText().toString();



//        String assId4JobNum = dataHolder.assignmentId.substring(0, 4);
//        String orgName4JobNum = result.getOrgName().substring(0, 4);

        model.jobNumber = createJobNumber(dataHolder.assignmentId.substring(0, 4));

        Log.i("HERE", "JOB NUMBER " + model.jobNumber);

        if (!dataHolder.assignmentAddress.equals("") && dataHolder.assignmentStartTime != null
                && dataHolder.assignmentDeadline != null && !model.name.equals("") && !model.contactPerson.equals("") &&
                !model.mobile.equals("")) {

            Toast.makeText(AddSelfAssignmentActivity.this, "Successfully created", Toast.LENGTH_SHORT).show();
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(model);
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(json).getAsJsonObject();
            dataHolder.assignmentDetails = o;

            AssignmentHolder holder = new AssignmentHolder();
            holder.data = new ArrayList<>();
            holder.data.add(dataHolder);

            CreateAssignmentTask task = new CreateAssignmentTask(this, this, holder);
            task.saveLocalCopytoDB();
            assignmentCreated = true;
            this.finish();
        } else {
            Toast.makeText(AddSelfAssignmentActivity.this, "Please Enter the Manditory Fields", Toast.LENGTH_LONG).show();
        }

        realm.close();

    }

    private String createJobNumber(String substringAssignmentID) {

        String assId4JobNum = substringAssignmentID;
        String orgName4JobNum = result.getOrgName().substring(0, 4);

        return orgName4JobNum.toLowerCase() + "-" + assId4JobNum;
    }

    @OnClick(R.id.saveAsDraftButton)
    public void saveAsDraft(){

        RealmQuery<RMCUser> query = realm.where(RMCUser.class);
        result = query.findFirst();


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(AddSelfAssignmentActivity.this);

        View mView = layoutInflaterAndroid.inflate(R.layout.assignment_draft_input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(AddSelfAssignmentActivity.this);
        alertDialogBuilderUserInput.setView(mView);


        assignmentDraftDesc = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here

                        Log.i("Hey ", " Send " + id);
                      //  if(!assignmentDraftDesc.getText().toString().contentEquals(""))
                        finallySaveDraft(assignmentDraftDesc.getText().toString());
                    }

                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                //
                                Log.i("Hey ", " Cancel " + id);
                          //      uploadImageChecking(id);
                                dialogBox.cancel();
                            }
                        });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();




    }

    private void finallySaveDraft(final String s) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String assignmentID = UUID.randomUUID().toString();

        try {
            if(startTimeInDateFormat!=null) {
                startTimeInDateFormat = format.parse(assignmentStartTImeUTC);
            }
            if(assignmentDeadlineUTC!=null) {
                assignmentDeadlineInDateFormat = format.parse(assignmentDeadlineUTC);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        realm = BDCloudUtils.getRealmBDCloudInstance();
        final RMCAssignmentDraft rmcAssignmentDraft = new RMCAssignmentDraft();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                rmcAssignmentDraft.setAssignmentId(assignmentID);
                rmcAssignmentDraft.setAddress(String.valueOf(address));
                rmcAssignmentDraft.setAssignmentDeadline(startTimeInDateFormat);
                rmcAssignmentDraft.setAssignmentStartTime(assignmentDeadlineInDateFormat);
                rmcAssignmentDraft.setEmail(email.getText().toString());
                rmcAssignmentDraft.setContactNumber(contactNum.getText().toString());
                rmcAssignmentDraft.setContactPerson(person.getText().toString());
                rmcAssignmentDraft.setJobNumber(createJobNumber(assignmentID.substring(0, 4)));
                rmcAssignmentDraft.setDraft_description(s);
                realm.copyToRealmOrUpdate(rmcAssignmentDraft);
             }
        });
        realm.close();
        Toast.makeText(AddSelfAssignmentActivity.this, "Assignment Draft Saved", Toast.LENGTH_SHORT).show();
        AddSelfAssignmentActivity.this.finish();
    }

    @OnClick(R.id.calendar_startDate)
    public void calenderStartDate() {

        dateAndTimeCalender = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, final int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                year1 = year;
                month1 = monthOfYear;
                date = dayOfMonth;
                str = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year);
//                deadline_editText.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year));
                final Calendar now = Calendar.getInstance();

                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        str += " " + hourOfDay + ":" + minute;
                        hours = hourOfDay;
                        min = minute;
                        dateAndTimeCalender.set(year1, month1, dayOfMonth, hours, min);

                        Date date1 = null;
                        // 2016-12-11T13:13:57.310Z
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                        //     dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+11:00"));
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        try {
                            date1 = dateFormat1.parse(str);
                            Log.i("DATE 1", "" + date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String gmtTime = dateFormat1.format(date1);
                        String gmtTime2 = dateFormat.format(date1);
                        startDate.setText(gmtTime);
                        assignmentStartTImeUTC = gmtTime2;
                        Log.i("ASSIGNMENT START TIME", " DateFormat1 " + gmtTime);
                        Log.i("ASSIGNMENT START TIME", " DateFormat " + gmtTime2);
                    }

                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), is24mode);
                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                timePickerDialog.setMinTime(Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND);
                timePickerDialog.show(getFragmentManager(), "timePicker");

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.show(getFragmentManager(), "datePicker");

    }

    @OnClick(R.id.calendar_endDate)
    public void calenderEndDate() {

        dateAndTimeCalender = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, final int dayOfMonth) {
                calendar2.set(year, monthOfYear, dayOfMonth);
                year1 = year;
                month1 = monthOfYear;
                date = dayOfMonth;
                str = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year);
//                deadline_editText.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year));
                final Calendar now = Calendar.getInstance();

                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        str += " " + hourOfDay + ":" + minute;
                        hours = hourOfDay;
                        min = minute;
                        //   endDate.setText(str);
                        Date date1 = null;
                        Date newDate = new Date();
                        // 2016-12-11T13:13:57.310Z
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                        try {
                            date1 = dateFormat1.parse(str);
                            addedOnUTC = dateFormat.format(newDate);
                            Log.i("DATE 1", "" + date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String gmtTime = dateFormat1.format(date1);
                        String gmtTime2 = dateFormat.format(date1);
                        endDate.setText(gmtTime);
                        assignmentDeadlineUTC = gmtTime2;
                        updatedOnUTC = addedOnUTC;
                        dateAndTimeCalender.set(year1, month1, dayOfMonth, hours, min);
                    }

                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), is24mode);
                now.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                Log.i("Current Time", "" + Calendar.HOUR + " " + Calendar.MINUTE);
                timePickerDialog.show(getFragmentManager(), "timePicker");
                timePickerDialog.setMinTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND);
            }
        }, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH));
        calendar2.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.show(getFragmentManager(), "datePicker");

    }

    @OnClick(R.id.cancel_create_assignment)
    public void cancel() {
        this.finish();
    }

    private void settingTypeFaces() {

        labelName.setTypeface(mFontRegular);
        name.setTypeface(mFontRegular);
        labelAddress.setTypeface(mFontRegular);
        address.setTypeface(mFontRegular);
        labelContactPerson.setTypeface(mFontRegular);
        labelContact.setTypeface(mFontRegular);
        contactNum.setTypeface(mFontRegular);
        person.setTypeface(mFontRegular);
        labelEmail.setTypeface(mFontRegular);
        email.setTypeface(mFontRegular);
        label_startDate.setTypeface(mFontRegular);
        startDate.setTypeface(mFontRegular);
        label_endDate.setTypeface(mFontRegular);
        endDate.setTypeface(mFontRegular);
        bottom_save_button.setTypeface(mFontRegular);
        bottom_saveAsDrafts_button.setTypeface(mFontRegular);

//        label_Reccurence.setTypeface(mFontRegular);
//        label_Reminders.setTypeface(mFontRegular);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

    }

}
