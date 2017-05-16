package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by mayanksaini on 05/10/16.
 */
public class OTPPasswordTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private String mobileNo;

    public OTPPasswordTask(Context context, RequestCallback requestCallback,
                           String mobileNo) {
        this.context = context;
        this.requestCallback = requestCallback;
        this.mobileNo = mobileNo;
    }

    public void mayBeVerifyOtp() {
        String url = Constants.BASE_URL + "user/otp?mobile="+mobileNo;
        Log.i("We have are URL "," "+url);

      try {
            BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService
                    (context, OTPPasswordTask.this);

            bdCloudPlatformService.executeHttpGet(url, context, Constants.verifyOTPCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        if (type == Constants.verifyOTPCallback) {

            Log.i("OTPPasswordTask Response Received"," is called");

            if (status == true) {
                try {
                    JSONObject obj = object.getJSONObject("data");
                    String code = obj.getString("code");

                    if(code.equalsIgnoreCase("OTP scheduled")){
                        requestCallback.onResponseReceived(object,true, "OTP scheduled",type);
                    }else{
                        requestCallback.onResponseReceived(object,false,"Failure",type);
                    }

                    if (INFO_LOG == true) {
                        Log.i("verifyOTPCallback---> ", code);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                requestCallback.onResponseReceived(object,false,null,type);
            }
        }
    }
}
