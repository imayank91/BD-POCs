package com.RareMediaCompany.BDPro.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.RMC.BDCloud.Android.BDCloudPlatformServiceAndroid;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.Android.UserAuthenticationTask;
import com.RareMediaCompany.BDPro.Helpers.PreferenceforApp;
import com.RareMediaCompany.BDPro.R;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.KeyEvent.KEYCODE_DEL;

/**
 * Created by mayanksaini on 16/12/16.
 */

public class OTPActivity extends BaseActivity implements RequestCallback {


    @BindView(R.id.first_otp)
    AppCompatEditText firstOtp;

    @BindView(R.id.second_otp)
    AppCompatEditText secondOtp;

    @BindView(R.id.third_otp)
    AppCompatEditText thirdOtp;

    @BindView(R.id.fourth_otp)
    AppCompatEditText fourthOtp;

    @BindView(R.id.fifth_otp)
    AppCompatEditText fifthOtp;

    @BindView(R.id.sixth_otp)
    AppCompatEditText sixthOtp;

    @BindView(R.id.OTP_sent_to_label)
    TextView otp_sendTo_label;

    @BindView(R.id.OTP_sent_to_number)
    TextView otp_to_number;

    @BindView(R.id.having_trouble_label)
    TextView havingTrouble;

    @BindView(R.id.click_here_label)
    TextView clickHereLabel;

    private BDCloudPlatformServiceAndroid bdCloudPlatformServiceAndroid;
    private PreferenceforApp prefs;
    private String mobileNo, mOTP, orgId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_enter_otp);
        ButterKnife.bind(this);
        prefs = new PreferenceforApp(this);
        mobileNo = getIntent().getStringExtra("mobileNo");

//        orgId = getResources().getString(R.string.org_id);

        progressDialog = new ProgressDialog(OTPActivity.this);
        otp_to_number.setText(mobileNo);

        otp_to_number.setTypeface(mFontRegular);
        otp_sendTo_label.setTypeface(mFontRegular);
        firstOtp.setTypeface(mFontRegular);
        secondOtp.setTypeface(mFontRegular);
        thirdOtp.setTypeface(mFontRegular);
        fourthOtp.setTypeface(mFontRegular);
        fifthOtp.setTypeface(mFontRegular);
        sixthOtp.setTypeface(mFontRegular);
        clickHereLabel.setTypeface(mFontRegular);
        havingTrouble.setTypeface(mFontRegular);

        setEditTextListeners();

//        firstOtp.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                openKeyBoard(firstOtp);
//            }
//        }, 400);
    }


    private void setEditTextListeners() {
        firstOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    secondOtp.requestFocus();
                }
            }
        });
        secondOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    thirdOtp.requestFocus();
                }
            }
        });
        thirdOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    fourthOtp.requestFocus();
                }
            }
        });
        fourthOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    fifthOtp.requestFocus();
                }
            }
        });
        fifthOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    sixthOtp.requestFocus();
                }
            }
        });
        sixthOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    closeKeyBoard();
                    checkInput();
                }
            }
        });


        setBackPressAction(secondOtp, firstOtp);
        setBackPressAction(thirdOtp, secondOtp);
        setBackPressAction(fourthOtp, thirdOtp);
        setBackPressAction(fifthOtp, fourthOtp);
        setBackPressAction(sixthOtp, fifthOtp);

    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected();
    }

    private void checkInput() {
        mOTP = firstOtp.getText().toString() +
                secondOtp.getText().toString() +
                thirdOtp.getText().toString() +
                fourthOtp.getText().toString() +
                fifthOtp.getText().toString() +
                sixthOtp.getText().toString();
        if (mOTP.matches("\\d{6}")) {
            if (isInternetAvailable(getApplicationContext())) {
                // input is valid
                progressDialog.setMessage("Verifying...");
                progressDialog.show();
                UserAuthenticationTask task = new UserAuthenticationTask(getApplicationContext(), this, "true", "custom",
                        mobileNo, mOTP, false, null, getResources().getString(R.string.orgId));
                task.getOauthToken();
            } else {
                showSnackbar(sixthOtp, "Please check your internet connection");
            }
        } else {
//            clearTextAndShowMessage(getResString(R.string.invalid_otp_code_input));
//            openKeyBoard(etOtpInput1);
        }
    }


    private void setBackPressAction(EditText editText, final View nextFocusable) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_DEL) {
                    EditText e = (EditText) v;
                    e.setText("");
//                    v.clearFocus();
                    v.post(new Runnable() {

                        @Override
                        public void run() {
                            if (nextFocusable instanceof EditText) {
                                ((EditText) nextFocusable).setText("");
                            }
                            nextFocusable.requestFocus();
                        }
                    });
                    return true;
                }
                return false;
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
                prefs.setPassword(mOTP);
                Intent intent = new Intent(OTPActivity.this, MyDashboardActivity.class);
                startActivity(intent);
                finish();
                Login.login.finish();
            } else {
                progressDialog.cancel();

                firstOtp.setText("");
                secondOtp.setText("");
                thirdOtp.setText("");
                fourthOtp.setText("");
                fifthOtp.setText("");
                sixthOtp.setText("");
                showSnackbar(firstOtp, message);
                firstOtp.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openKeyBoard(firstOtp);
                    }
                }, 1500);

//                Toast.makeText(OTPActivity.this, message, Toast.LENGTH_SHORT).show();

            }
        }
    }
}
