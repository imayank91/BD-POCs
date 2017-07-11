package com.RareMediaCompany.BDPro.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.RareMediaCompany.BDPro.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sidd on 1/9/17.
 */

public class FilterActivity extends BaseActivity {


    @BindView(R.id.fromStartDate)
    TextView fromStartDate;

    @BindView(R.id.toStartDate)
    TextView toStartDate;

    @BindView(R.id.fromEndDate)
    TextView fromEndDate;

    @BindView(R.id.toEndDate)
    TextView toEndDate;

    @BindView(R.id.assignedByCheckbox)
    CheckBox assignByCheckBox;

    @BindView(R.id.startDateCheckbox)
    CheckBox startDateCheckBox;

    @BindView(R.id.endDateCheckbox)
    CheckBox endDateCheckBox;

    @BindView(R.id.radioGroup_assignmentType)
    RadioGroup radioGroupAssignmentType;

    @BindView(R.id.selfAssignedFilter)
    RadioButton radioButtonSelf;

    @BindView(R.id.assignedByManagerFilter)
    RadioButton radioButtonManager;

    SharedPreferences myFilterPreferences;
    public static final String FILTERPREFS = "filterprefs";
    private Calendar calendar, calendar1, dateAndTimeCalender;
    private String str;
    private Boolean is24mode = false;
    int year1, month1, date, hours, min;
    String gmtTime = null;
    String mydatefor = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        calendar1 = Calendar.getInstance();
        calendar = Calendar.getInstance();
        myFilterPreferences = getSharedPreferences(FILTERPREFS, Context.MODE_PRIVATE);

        if(myFilterPreferences.getBoolean("filterSD",false)){
            startDateCheckBox.setChecked(true);
            fromStartDate.setText(myFilterPreferences.getString("fromStartDate",""));
            toStartDate.setText(myFilterPreferences.getString("toStartDate",""));
        }

        if(myFilterPreferences.getBoolean("filterED", false)){
            endDateCheckBox.setChecked(true);
            fromEndDate.setText(myFilterPreferences.getString("fromEndDate",""));
            toEndDate.setText(myFilterPreferences.getString("toEndDate",""));
        }

        if(myFilterPreferences.getBoolean("filterAT", false)){
            assignByCheckBox.setChecked(true);

            if (myFilterPreferences.getString("Self", "").contentEquals("Self")) {
                radioButtonSelf.setChecked(true);
            } else if (myFilterPreferences.getString("Manager", "").contentEquals("Manager")) {
                radioButtonManager.setChecked(true);
            }

        }

        startDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!isChecked) {
                    removeStartDate();
                }else{
                    if(fromStartDate.getText().toString().contentEquals("From Date")
                            && toStartDate.getText().toString().contentEquals("To Date")){
                        Snackbar.make(findViewById(R.id.filterParentLayout), "Enter From Date & To Date for StartDate",Snackbar.LENGTH_LONG).show();
                        //    startDateCheckBox.setChecked(false);
                    }
                }
            }
        });

        endDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    removeEndDate();
                }else{
                    if(fromEndDate.getText().toString().contentEquals("From Date")
                            && toEndDate.getText().toString().contentEquals("To Date")){
                        Snackbar.make(findViewById(R.id.filterParentLayout), "Enter From Date & To Date for EndDate",Snackbar.LENGTH_LONG).show();
                        //      endDateCheckBox.setChecked(false);
                    }
                }
            }
        });

        assignByCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    removeAssignmentType();
                }
                else{
                    if(!radioButtonSelf.isChecked() && !radioButtonManager.isChecked()){
                        Snackbar.make(findViewById(R.id.filterParentLayout), "Select an Assignment Type", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });



        radioGroupAssignmentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Log.i("Checked ID ",""+checkedId);

                if(radioButtonManager.isChecked() || radioButtonSelf.isChecked()){
                    assignByCheckBox.setChecked(true);
                }else {
                    assignByCheckBox.setChecked(false);
                }

            }
        });

    }

    @OnClick(R.id.closeFilter)
    public void closeFilter(){
        finish();
    }

    @OnClick(R.id.applyFilter)
    public void applyFilter(){

        if(startDateCheckBox.isChecked()){
            if(fromStartDate.getText().toString().equals("From Date") || toStartDate.getText().toString().equals("To Date")){
                Snackbar.make(findViewById(R.id.filterParentLayout), "Enter both dates for StartDate Filter",Snackbar.LENGTH_LONG).show();
            }else
            {
                finallyApplyFilter();
            }
        }else
        if(endDateCheckBox.isChecked()){
            if(fromEndDate.getText().toString().equals("From Date") || toEndDate.getText().toString().equals("To Date")){
                Snackbar.make(findViewById(R.id.filterParentLayout), "Enter both dates for EndDate Filter",Snackbar.LENGTH_LONG).show();

            }else{
                finallyApplyFilter();
            }
        } else {
            finallyApplyFilter();
        }
    }

    private void finallyApplyFilter() {

        if (assignByCheckBox.isChecked()) {
            Log.i("Hello", "Assigned Checked");
        }

        if (startDateCheckBox.isChecked()) {

            SharedPreferences.Editor editor = myFilterPreferences.edit();
            editor.putBoolean("filterSD", true);
            editor.putString("fromStartDate", fromStartDate.getText().toString());
            editor.putString("toStartDate", toStartDate.getText().toString());
            editor.apply();
        }

        if (endDateCheckBox.isChecked()) {

            SharedPreferences.Editor editor = myFilterPreferences.edit();
            editor.putBoolean("filterED", true);
            editor.putString("fromEndDate", fromEndDate.getText().toString());
            editor.putString("toEndDate", toEndDate.getText().toString());
            editor.apply();
        }

        if (assignByCheckBox.isChecked()) {
            SharedPreferences.Editor editor = myFilterPreferences.edit();
            editor.putBoolean("filterAT", true);

            if (radioButtonSelf.isChecked()) {
                assignByCheckBox.setChecked(true);
                editor.putString("Self", "Self");
                editor.remove("Manager");
            } else if (radioButtonManager.isChecked()) {
                editor.putString("Manager", "Manager");
                editor.remove("Self");
            }

            editor.apply();
            //editor.putString("Self", )
        }



        Toast.makeText(FilterActivity.this, "Filter Applied", Toast.LENGTH_SHORT).show();
        finish();

    }

    @OnClick(R.id.fromStartDate)
    public void chooseFromStartDate(){
        mydatefor = "fromSD";
        getDate();
    }

    @OnClick(R.id.toStartDate)
    public void chooseToStartDate(){
        mydatefor = "toSD";
        getDate();
    }

    @OnClick(R.id.fromEndDate)
    public void chooseFromEndDate(){
        mydatefor = "fromED";
        getDate1();
    }

    @OnClick(R.id.toEndDate)
    public void chooseToEndDate(){
        mydatefor = "toED";
        getDate1();
    }

    private void getDate() {

        dateAndTimeCalender = Calendar.getInstance();
        final DatePickerDialog datePickerDialog =  DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
                        dateFormat1.setTimeZone(TimeZone.getTimeZone("gmt"));
                        try {
                            date1 = dateFormat1.parse(str);
                            Log.i("DATE 1", "" + date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        gmtTime = dateFormat1.format(date1);
                        String gmtTime2 = dateFormat.format(date1);

                        if(mydatefor.contentEquals("fromSD")){
                            fromStartDate.setText(gmtTime);
                        }else if(mydatefor.contentEquals("toSD")){
                            toStartDate.setText(gmtTime);
                            try {
                                calendar.setTime(dateFormat1.parse(fromStartDate.getText().toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            startDateCheckBox.setChecked(true);
                        }else if(mydatefor.contentEquals("fromED")){
                            fromEndDate.setText(gmtTime);
                        }else {
                            toEndDate.setText(gmtTime);
                            try {
                                calendar.setTime(dateFormat1.parse(fromEndDate.getText().toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            endDateCheckBox.setChecked(true);
                        }
                    }

                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), is24mode);
                timePickerDialog.show(getFragmentManager(), "timePicker");

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if(!fromStartDate.getText().toString().contentEquals("From Date")){
            datePickerDialog.setMinDate(calendar);
        }

        datePickerDialog.show(getFragmentManager(), "datePicker");
    }



    private void getDate1() {

        dateAndTimeCalender = Calendar.getInstance();
        final DatePickerDialog datePickerDialog =  DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, final int dayOfMonth) {
                calendar1.set(year, monthOfYear, dayOfMonth);
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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
                        dateFormat1.setTimeZone(TimeZone.getTimeZone("gmt"));
                        try {
                            date1 = dateFormat1.parse(str);
                            Log.i("DATE 1", "" + date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        gmtTime = dateFormat1.format(date1);
                        String gmtTime2 = dateFormat.format(date1);

                        if(mydatefor.contentEquals("fromSD")){
                            fromStartDate.setText(gmtTime);
                        }else if(mydatefor.contentEquals("toSD")){
                            toStartDate.setText(gmtTime);
                            startDateCheckBox.setChecked(true);
                        }else if(mydatefor.contentEquals("fromED")){
                            fromEndDate.setText(gmtTime);
                        }else {
                            toEndDate.setText(gmtTime);
                            try {
                                calendar1.setTime(dateFormat1.parse(fromEndDate.getText().toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            endDateCheckBox.setChecked(true);
                        }
                    }

                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), is24mode);
                timePickerDialog.show(getFragmentManager(), "timePicker");

            }
        }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
        if(!fromEndDate.getText().toString().contentEquals("From Date")){
            datePickerDialog.setMinDate(calendar1);
        }

        datePickerDialog.show(getFragmentManager(), "datePicker");
    }


    @OnClick(R.id.clearFilter)
    public void clearFilter(){

        startDateCheckBox.setChecked(false);
        endDateCheckBox.setChecked(false);
        assignByCheckBox.setChecked(false);
        radioButtonSelf.setChecked(false);
        radioButtonManager.setChecked(false);
        removeStartDate();
        removeEndDate();
        removeAssignmentType();
        Snackbar.make(findViewById(R.id.filterParentLayout), "Removed All Filters...", Snackbar.LENGTH_LONG).show();
    }

    private void removeAssignmentType() {
        SharedPreferences.Editor editor = myFilterPreferences.edit();
        editor.remove("filterAT");
        editor.remove("Self");
        editor.remove("Manager");
        editor.apply();
    }

    private void removeEndDate() {
        fromEndDate.setText("From Date");
        toEndDate.setText("To Date");
        SharedPreferences.Editor editor = myFilterPreferences.edit();
        editor.remove("filterED");
        editor.remove("fromEndDate");
        editor.remove("toEndDate");
        editor.apply();
    }

    private void removeStartDate() {
        fromStartDate.setText("From Date");
        toStartDate.setText("To Date");
        SharedPreferences.Editor editor = myFilterPreferences.edit();
        editor.remove("filterSD");
        editor.remove("fromStartDate");
        editor.remove("toStartDate");
        editor.apply();
    }
}
