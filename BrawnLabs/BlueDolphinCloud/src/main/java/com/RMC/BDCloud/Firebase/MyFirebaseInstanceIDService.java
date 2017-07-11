package com.RMC.BDCloud.Firebase;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.Android.BDCloudPlatformServiceAndroid;
import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.Android.UserAuthenticationTask;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by mayanksaini on 09/11/16.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements RequestCallback{

    private static final String TAG = "MyFirebaseIIDService";
    private BDPreferences prefs;
    RequestCallback requestCallback;

    public MyFirebaseInstanceIDService (){
        requestCallback = this;
    }

    @Override
    public void onTokenRefresh() {
        prefs = new BDPreferences(getApplicationContext());
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        prefs.setFirebaseToken(refreshedToken);
        //Displaying token on logcat
        if(INFO_LOG == true) {
            Log.i(getClass().getSimpleName() +" onTokenRefresh" , refreshedToken);
        }

//        sendRegistrationToServer(refreshedToken);

    }

    public void sendRegistrationToServer(String token, Context context) {

        prefs = new BDPreferences(context);

        Realm realm = BDCloudUtils.getRealmBDCloudInstance();
        RMCUser user = realm.where(RMCUser.class).equalTo("isActive",true).findFirst();

        String mobile = user.getMobileNo();
        String otpToken = user.getOtpToken();
        String imeiId = BDCloudUtils.getDeviceId(context);
        String oauthToken = user.getToken();
        String userId = user.getUserId();

        realm.close();
        String url = Constants.BASE_URL + "user";

        JSONObject jObj = new JSONObject();
        JSONObject jObj1 = new JSONObject();
        try {
            jObj1.put("authorization",oauthToken);
            jObj1.put("userId",userId);

            jObj.put(Constants.LOGIN_TYPE, Constants.MOBILE);
            jObj.put(Constants.MOBILE, mobile);
            jObj.put(Constants.OTP_TOKEN, otpToken);
            jObj.put(Constants.DEVICE_TYPE, Constants.DEVICE_ANDROID);
            jObj.put(Constants.DEVICE_TOKEN, token);
            jObj.put(Constants.IMEI_ID, imeiId);

            BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService(MyFirebaseInstanceIDService.this,
                    requestCallback);

            bdCloudPlatformService.executeHttpPut(url, jObj, context, Constants.sendRegistrationToServerCallback, jObj1,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        if(type == Constants.sendRegistrationToServerCallback) {
            if(status == true) {
                try {
                    String msg  = object.getString("message");
                    int statusCode = object.getInt("statusCode");

                    if(statusCode == 200) {
                        prefs.firebaseRegistered(true);
                        if(INFO_LOG == true) {
                            Log.i(getClass().getSimpleName() +" sendRegistrationToServer","Firebase Token successfully registered");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}