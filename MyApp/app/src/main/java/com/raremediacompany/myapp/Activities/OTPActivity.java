package com.raremediacompany.myapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.RMC.BDCloud.Android.BDCloudPlatformServiceAndroid;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.Android.UserAuthenticationTask;
import com.raremediacompany.myapp.Helpers.PreferenceforApp;
import com.raremediacompany.myapp.R;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.KeyEvent.KEYCODE_DEL;

/**
 * Created by mayanksaini on 16/12/16.
 */

public class OTPActivity extends BaseActivity implements RequestCallback {


//    @BindView(R.id.first_otp)
    AppCompatEditText firstOtp;

//    @BindView(R.id.second_otp)
    AppCompatEditText secondOtp;

//    @BindView(R.id.third_otp)
    AppCompatEditText thirdOtp;

//    @BindView(R.id.fourth_otp)
    AppCompatEditText fourthOtp;

//    @BindView(R.id.fifth_otp)
    AppCompatEditText fifthOtp;

//    @BindView(R.id.sixth_otp)
    AppCompatEditText sixthOtp;

//    @BindView(R.id.OTP_sent_to_label)
    TextView otp_sendTo_label;

//    @BindView(R.id.OTP_sent_to_number)
    TextView otp_to_number;

  //  @BindView(R.id.having_trouble_label)
    TextView havingTrouble;

   // @BindView(R.id.click_here_label)
    TextView clickHereLabel;

    private BDCloudPlatformServiceAndroid bdCloudPlatformServiceAndroid;
    private PreferenceforApp prefs;
    private String mobileNo, otp, orgId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_enter_otp);
 //       ButterKnife.bind(this);
        firstOtp = (AppCompatEditText) findViewById(R.id.first_otp) ;
        secondOtp = (AppCompatEditText) findViewById(R.id.second_otp);
        thirdOtp = (AppCompatEditText)findViewById(R.id.third_otp);
        fourthOtp = (AppCompatEditText)findViewById(R.id.fourth_otp);
        fifthOtp = (AppCompatEditText)findViewById(R.id.fifth_otp);
        sixthOtp = (AppCompatEditText)findViewById(R.id.sixth_otp);
        otp_sendTo_label = (TextView)findViewById(R.id.OTP_sent_to_label);
        otp_to_number = (TextView)findViewById(R.id.OTP_sent_to_number);
        havingTrouble = (TextView)findViewById(R.id.having_trouble_label);
        clickHereLabel = (TextView)findViewById(R.id.click_here_label);

        prefs = new PreferenceforApp(this);
        mobileNo = getIntent().getStringExtra("mobileNo");



//        orgId = getResources().getString(R.string.org_id);

        progressDialog = new ProgressDialog(OTPActivity.this);
        otp_to_number.setText(mobileNo);

//        otp_to_number.setTypeface(mFontRegular);
//        otp_sendTo_label.setTypeface(mFontRegular);
//        firstOtp.setTypeface(mFontRegular);
//        secondOtp.setTypeface(mFontRegular);
//        thirdOtp.setTypeface(mFontRegular);
//        fourthOtp.setTypeface(mFontRegular);
//        fifthOtp.setTypeface(mFontRegular);
//        sixthOtp.setTypeface(mFontRegular);
//        clickHereLabel.setTypeface(mFontRegular);
//        havingTrouble.setTypeface(mFontRegular);

        firstOtp.requestFocus();

        firstOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length1 = firstOtp.getText().length();
                if (length1 == 1) {
                    secondOtp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        secondOtp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_DEL) {
                    firstOtp.requestFocus();
                }
                return false;
            }
        });


        secondOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length2 = secondOtp.getText().length();
                if (length2 == 1) {
                    thirdOtp.requestFocus();
                }

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        thirdOtp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_DEL) {
                    secondOtp.requestFocus();
                }
                return false;
            }
        });


        thirdOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length3 = thirdOtp.getText().length();
                if (length3 == 1) {
                    fourthOtp.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        fourthOtp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_DEL) {
                    thirdOtp.requestFocus();
                }
                return false;
            }
        });


        fourthOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length4 = fourthOtp.getText().length();
                if (length4 == 1) {
                    fifthOtp.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fifthOtp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_DEL) {
                    fourthOtp.requestFocus();
                }
                return false;
            }
        });


        fifthOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length5 = fifthOtp.getText().length();
                if (length5 == 1) {
                    sixthOtp.requestFocus();
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sixthOtp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_DEL) {
                    fifthOtp.requestFocus();
                }
                return false;
            }
        });


        sixthOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                progressDialog.setMessage("Verifying");
                progressDialog.show();


                otp = firstOtp.getText().toString() + secondOtp.getText().toString() +
                        thirdOtp.getText().toString() + fourthOtp.getText().toString() +
                        fifthOtp.getText().toString() + sixthOtp.getText().toString();

                UserAuthenticationTask task = new UserAuthenticationTask(OTPActivity.this, OTPActivity.this, "true", "custom",
                        mobileNo, otp, false, null, getResources().getString(R.string.orgId));
                task.getOauthToken();
            }
        });

    }


    @Override

    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.Oauth2Callback) {
            if (status) {
                progressDialog.cancel();
                Toast.makeText(OTPActivity.this, "Activation Successful", Toast.LENGTH_SHORT).show();
                prefs.setUserActivated(true);
                prefs.setUserId(mobileNo);
                prefs.setPassword(otp);
                BDPreferences prefs = new BDPreferences(getApplicationContext());
                prefs.setOrganisationId(getString(R.string.orgId));
                Intent intent = new Intent(OTPActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                Login.login.finish();
            } else {
                progressDialog.cancel();
                Toast.makeText(OTPActivity.this, "Wrong OTP, Please try again", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
