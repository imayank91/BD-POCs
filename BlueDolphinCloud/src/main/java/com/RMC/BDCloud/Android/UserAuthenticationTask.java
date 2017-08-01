package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.RealmDB.Model.RMCUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mayanksaini on 17/10/16.
 */
import io.realm.Realm;
import io.realm.RealmResults;

import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

public class UserAuthenticationTask implements RequestCallback {

    private RequestCallback requestCallback;
    private Context context;
    private boolean requestRefreshToken;
    private String selfRequest, signupType, mobileNo, otpToken, refreshToken, orgID, userName, orgName, userId;
    private Realm realm;

    public UserAuthenticationTask(Context context, RequestCallback requestCallback,
                                  String selfRequest, String signupType, String mobileNo,
                                  String otpToken, boolean refreshToken, String refreshTokenStr, String orgId) {
        this.context = context;
        this.requestCallback = requestCallback;
        this.requestRefreshToken = refreshToken;
        this.selfRequest = selfRequest;
        this.signupType = signupType;
        this.mobileNo = mobileNo;
        this.otpToken = otpToken;
        this.refreshToken = refreshTokenStr;
        this.orgID = orgId;
    }

    public void getOauthToken() {
        realm = BDCloudUtils.getRealmBDCloudInstance();
        String userId = null;

        if (requestRefreshToken == true) {
            RMCUser user = realm.where(RMCUser.class).equalTo("isActive", true).findFirst();
            userId = user.getUserId();
        }
        realm.close();
        String url = Constants.BASE_URL + "auth";

        JSONObject jObj = new JSONObject();
        try {
            if (requestRefreshToken == true) {
                jObj.put(Constants.GRANT_TYPE, Constants.REFRESH_TOKEN);
                jObj.put(Constants.REFRESH_TOKEN, refreshToken);
                jObj.put(Constants.USER_ID, userId);
            } else {
                jObj.put(Constants.GRANT_TYPE, Constants.ACCESS_TOKEN);
                jObj.put(Constants.LOGIN_TYPE, Constants.MOBILE);
                jObj.put(Constants.MOBILE, mobileNo);
                jObj.put(Constants.OTP_TOKEN, otpToken);
            }

            jObj.put(Constants.SELF_REQUEST, Boolean.valueOf(selfRequest));
//            jObj.put(Constants.SIGNUP_TYPE, signupType);  not required in dev api

            BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService(context,
                    UserAuthenticationTask.this);

            bdCloudPlatformService.executeHttpPost(url, jObj, context, Constants.Oauth2Callback, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {
        realm = BDCloudUtils.getRealmBDCloudInstance();
        boolean flag = false;

        if (type == Constants.Oauth2Callback) {
            if (status == true) {
                RealmResults<RMCUser> rmcUsers = realm.where(RMCUser.class).findAll();
                if (rmcUsers != null && rmcUsers.size() > 0) {
                    for (final RMCUser user : rmcUsers) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                user.setActive(false);
                                realm.copyToRealmOrUpdate(user);
                            }
                        });
                    }
                }
                try {
                    JSONObject obj = object.getJSONObject("data");
                    JSONArray arr = obj.getJSONArray("accessToken");
                    JSONArray arr1 = obj.getJSONArray("refreshToken");

                    for (int i = 0; i < arr.length(); i++) {
                        final String orgId = arr.getJSONObject(i).getString("organizationId");
                        if (!orgID.equalsIgnoreCase(orgId)) {
                            continue;
                        }
                        flag = true;
                        final String token = arr.getJSONObject(i).getString("token");
                        final String refreshToken = arr1.getJSONObject(0).getString("token");
                        userId = arr.getJSONObject(i).getString("userId");
                        RMCUser user = realm.where(RMCUser.class).equalTo("userId", userId).findFirst();
                        if (user != null) {
                            userName = user.getUserName();
                            orgName = user.getOrgName();
                            mobileNo = user.getMobileNo();
                            otpToken = user.getOtpToken();
                            userId = user.getUserId();

                        } else {
                            userName = arr.getJSONObject(i).getString("userName");
                            orgName = arr.getJSONObject(i).getString("organizationName");
                        }
                        final long expires = arr.getJSONObject(i).getLong("expires");

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // Add a user
                                RMCUser user = new RMCUser();
                                if (requestRefreshToken == false) {
                                    user.setMobileNo(mobileNo);
                                    user.setUserName(userName);
                                    user.setOrgId(orgId);
                                    user.setToken(token);
                                    user.setRefreshToken(refreshToken);
                                    user.setUserId(userId);
                                    user.setOrgName(orgName);
                                    user.setActive(true);
                                    user.setExpires(expires);
                                    user.setOtpToken(otpToken);
                                } else {
                                    user.setActive(true);
                                    user.setToken(token);
                                    user.setRefreshToken(refreshToken);
                                    user.setUserName(userName);
                                    user.setOrgName(orgName);
                                    user.setOrgId(orgId);
                                    user.setUserId(userId);
                                    user.setExpires(expires);
                                    user.setMobileNo(mobileNo);
                                    user.setOtpToken(otpToken);
                                }
                                realm.copyToRealmOrUpdate(user);
                            }
                        });

                        if (INFO_LOG == true) {
                            Log.i("Oauth2Callback response position (" + i + ") ", orgId + " " + userId + " " + orgName);
                            final RMCUser user1 = realm.where(RMCUser.class).findFirst();
                            Log.i("Realm RMCUser collection ", "orgid- " + user1.getOrgId() + " userid-" + user1.getUserId() + " orgname-" + user1.getOrgName());
                        }

                        requestCallback.onResponseReceived(object, status, null, type);

                    }
                    realm.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(flag == false) {
                    requestCallback.onResponseReceived(object, false, "Number not found", type);
                }
            } else {

                try {
                    JSONObject obj = object;
                    String msg = obj.getString("message");

                    if (msg == null) {
                        msg = "Wrong OTP..Please try again";
                    }
                    requestCallback.onResponseReceived(obj, false, msg, type);

                } catch (Exception e) {
                    requestCallback.onResponseReceived(null, false, "Wrong OTP..Please try again", type);
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}