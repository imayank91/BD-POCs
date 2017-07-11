package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.RealmDB.Model.RMCUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by mayanksaini on 06/04/17.
 */

public class InitialiseSDKTask implements RequestCallback {

    private String secretKey, orgId, firstName, lastName, email, mobile;
    private Context context;
    private RequestCallback requestCallback;
    private Realm realm;

    public InitialiseSDKTask(Context context, RequestCallback requestCallback, String secretKey, String orgId,
                             String firstName, String lastName, String mobile, String email) {
        this.context = context;
        this.requestCallback = requestCallback;
        this.secretKey = secretKey;
        this.orgId = orgId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
    }

    public void registerSDK() {

        realm = BDCloudUtils.getRealmBDCloudInstance();

        String url = Constants.BASE_URL + "auth";

        JSONObject jObj = new JSONObject();
        try {
            jObj.put(Constants.GRANT_TYPE, Constants.ACCESS_TOKEN);
            jObj.put(Constants.SELF_REQUEST, true);
            jObj.put(Constants.LOGIN_TYPE, Constants.SECRET_KEY);
            jObj.put(Constants.ORG_ID, orgId);
            if (mobile != null && !mobile.equalsIgnoreCase("")) {
                jObj.put(Constants.MOBILE, mobile);
            } else {
                jObj.put(Constants.EMAIL, email);
            }
            jObj.put(Constants.FIRST_NAME, firstName);
            jObj.put(Constants.LAST_NAME, lastName);
            jObj.put(Constants.SECRET_KEY, secretKey);


            BDCloudPlatformServiceAndroid bdCloudPlatformService = BDCloudUtils.getPlatformService(context,
                    InitialiseSDKTask.this);

            bdCloudPlatformService.executeHttpPost(url, jObj, context, Constants.initialiseSDKCallback, null, null);


        } catch (Exception e) {
            if(e != null) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.initialiseSDKCallback) {
            if (status == true) {
                RealmResults<RMCUser> rmcUsers = realm.where(RMCUser.class).findAll();
                if (rmcUsers != null && rmcUsers.size() > 0) {
                    for (final RMCUser user : rmcUsers) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                user.setActive(false);
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
                        if (!orgId.equalsIgnoreCase(orgId)) {
                            continue;
                        }
                        final String token = arr.getJSONObject(i).getString("token");
                        final String refreshToken = arr1.getJSONObject(0).getString("token");
                        final String userId = arr.getJSONObject(i).getString("userId");
                        final String userName = arr.getJSONObject(i).getString("userName");
                        final String orgName = arr.getJSONObject(i).getString("organizationName");
                        final long expires = arr.getJSONObject(i).getLong("expires");

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // Add a user
                                RMCUser user = new RMCUser();
                                if (mobile != null && !mobile.equalsIgnoreCase("")) {
                                    user.setMobileNo(mobile);
                                } else {
                                    user.setMobileNo(email);
                                }
                                user.setUserName(userName);
                                user.setOrgId(orgId);
                                user.setToken(token);
                                user.setRefreshToken(refreshToken);
                                user.setUserId(userId);
                                user.setOrgName(orgName);
                                user.setActive(true);
                                user.setExpires(expires);
                                user.setOtpToken(secretKey);
                                realm.copyToRealmOrUpdate(user);
                            }
                        });

                        if (INFO_LOG == true) {
                            Log.i("Oauth2Callback response position (" + i + ") ", orgId + " " + userId + " " + orgName);
                            final RMCUser user1 = realm.where(RMCUser.class).findFirst();
                            Log.i("Realm RMCUser collection ", "orgid- " + user1.getOrgId() + " userid-" + user1.getUserId() + " orgname-" + user1.getOrgName());
                        }

                        if (requestCallback != null) {
                            requestCallback.onResponseReceived(object, status, null, type);
                        }
                    }
                    realm.close();
                    BDCloudUtils.startSync(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
