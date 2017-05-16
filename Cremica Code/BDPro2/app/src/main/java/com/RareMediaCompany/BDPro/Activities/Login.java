package com.RareMediaCompany.BDPro.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.RMC.BDCloud.Android.BDCloudPlatformServiceAndroid;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.OTPPasswordTask;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RareMediaCompany.BDPro.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayanksaini on 16/12/16.
 */

public class Login extends BaseActivity implements RequestCallback {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static String TAG = "LoginActivity";
    public static Login login;
    @BindView(R.id.mobileNumET)
    AppCompatEditText mobileNoET;
    @BindView(R.id.send_otp_inactive)
    ImageButton sendOtpInactive;
    @BindView(R.id.send_otp_active)
    ImageButton sendOtpactive;
    @BindView(R.id.parentLayout)
    LinearLayout parentLayout;
    @BindView(R.id.label_to_enter_mobile)
    TextView label_to_enter_mobile;
    private BDCloudPlatformServiceAndroid bdCloudPlatformServiceAndroid;
    private String mobileNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_entermobilenum);
        ButterKnife.bind(this);
        login = this;
        label_to_enter_mobile.setTypeface(mFontRegular);
        mobileNoET.setTypeface(mFontRegular);

        checkAndRequestPermissions();

        mobileNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mobileNoET.length() >= 10) {
                    sendOtpInactive.setVisibility(View.GONE);
                    sendOtpactive.setVisibility(View.VISIBLE);

                    View view = Login.this.getCurrentFocus();
                    if(view!=null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else if (mobileNoET.length() <= 10) {
                    sendOtpInactive.setVisibility(View.VISIBLE);
                    sendOtpactive.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.send_otp_active)
    public void sendOtpClicked() {

        Log.i("Send OTP ", "Clicked");

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Requesting OTP");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mobileNo = mobileNoET.getText().toString();

        OTPPasswordTask task = new OTPPasswordTask(Login.this, Login.this, mobileNo);
        task.mayBeVerifyOtp();

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.verifyOTPCallback) {

            Log.i("Login Response Received", " is called");

            if (status == true) {
                if (message.equalsIgnoreCase("OTP scheduled")) {
                    progressDialog.cancel();
                    Intent i = new Intent(getApplicationContext(), OTPActivity.class);
                    i.putExtra("mobileNo", mobileNo);
                    startActivity(i);
                }

            } else {
                progressDialog.cancel();
                Snackbar.make(parentLayout, "Number not found...Contact admin", Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int readPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int callPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int read_external_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write_external_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int bluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetoothAdmin = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
//        int readAccount = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
//        int readSyncSettings = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SYNC_SETTINGS);
//        int writeSyncSettings = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SYNC_SETTINGS);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (callPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }


        if (read_external_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if (write_external_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (bluetooth != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
        }

        if (bluetoothAdmin != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
//
//        if (writeSyncSettings != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.WRITE_SYNC_SETTINGS);
//        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH_ADMIN, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.WRITE_SYNC_SETTINGS, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if ((perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                            (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) &&
                            (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) &&
                            (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                            (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                            (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                            && (perms.get(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED)
                            &&
                            (perms.get(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED)
//                            (perms.get(Manifest.permission.WRITE_SYNC_SETTINGS) == PackageManager.PERMISSION_GRANTED)
                            )  {

                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN)
//                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_SYNC_SETTINGS)
                                ) {

                            showDialogOK("Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    finish();
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            finish();
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

}
