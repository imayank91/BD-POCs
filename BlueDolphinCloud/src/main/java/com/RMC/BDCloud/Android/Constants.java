package com.RMC.BDCloud.Android;

import android.content.Context;

/**
 * Created by mayanksaini on 18/10/16.
 */

public class Constants {

    public static Context baseContext ;

   // public static String BASE_URL = "https://swhuyj3x0d.execute-api.ap-southeast-1.amazonaws.com/bd/live/";

    //  DEV URL
  public static String BASE_URL = "https://kxjakkoxj3.execute-api.ap-southeast-1.amazonaws.com/bd/dev/";

//   public static String BASE_URL = "https://dqxr67yajg.execute-api.ap-southeast-1.amazonaws.com/bd/staging/";

 //    public static String BASE_URL = "https://dqxr67yajg.execute-api.ap-southeast-1.amazonaws.com/bd/staging/";

//    public static String BASE_URL = "https://01ly64puzb.execute-api.ap-south-1.amazonaws.com/bd/live/";

//    public static String BASE_URL = "https://0uvkmcic37.execute-api.ap-south-1.amazonaws.com/bd/live/";


    public static String USER_ID = "userId";
    public static String GRANT_TYPE = "grantType";
    public static String SELF_REQUEST = "selfRequest";
    public static String SIGNUP_TYPE = "signUpType";
    public static String LOGIN_TYPE = "loginType";
    public static String EMAIL = "email";
    public static String PASSWORD = "password";
    public static String OTP_TOKEN = "otpToken";
    public static String UPDATE_PASSWORD = "updatePassword";
    public static String ACCESS_TOKEN = "accessToken";
    public static String REFRESH_TOKEN = "refreshToken";
    public static String CUSTOM_TYPE = "custom";
    public static String GOOGLE_TYPE = "google";
    public static String FB_TYPE = "facebook";
    public static String IMEI_ID = "imeiId";
    public static String DEVICE_TYPE = "deviceType";
    public static String DEVICE_TOKEN = "deviceToken";
    public static String DEVICE_ANDROID = "android";
    public static String MOBILE = "mobile";
    public static String SECRET_KEY = "secretKey";
    public static String PREFS_NAME = "bdCloudPrefs";
    public static String ORG_ID = "organizationId";
    public static String FIRST_NAME = "firstName";
    public static String LAST_NAME = "lastName";


    //ModuleType
    public static String BDCLOUD_SCHEMA = "bdcloud.realm";
    public static int BDCLOUD_SCHEMA_VERSION = 10;



    //Request Callbacks Type
    public static int Oauth2Callback = 1;
    public static int verifyOTPCallback = 2;
    public static int updateOTPCallback = 3;
    public static int postCheckinsCallback = 4;
    public static int createAssignmentCallback = 5;
    public static int checkUserStatusCallback = 6;
    public static int pushNotificationCallback = 7;
    public static int sendRegistrationToServerCallback = 8;
    public static int downloadAssignmentsCallback = 9;
    public static int uploadPhotoCallback = 10;
    public static int getBeaconsCallback = 11;
    public static int deleteImageCallback = 12;
    public static int checkAssignmentCallback = 13;

    public static int submitBeaconsDataTaskCallback = 15;

    public static int downloadDObjectsCallback = 14;
    public static int updateAssignmentCallback = 16;
    public static int downloadPlaceCallback = 17;
    public static int initialiseSDKCallback = 18;



    public static final String ACCOUNT = "BDCloud";
    public static final int SYNC_FREQUENCY = 10 * 60; //in seconds

    public static final String IDENTITY_POOL_ID = "ap-northeast-1:a0c55baf-0ee2-4ffa-90e8-901d3b14c45b";
    public static final String BUCKET_NAME = "bd-bucket-tokyo";
    public static final String CLOUDFRONT_URL = "di4woccwc7kdj.cloudfront.net/";

    //Enums
    public enum checkinCategory {
        Transient ("Transient"),
        Non_Transient ("Non-Transient");

        private String name;

        private checkinCategory(String s) {
            name = s;
        }
    }

    public enum checkinType {
        Photo ("Photo"),
        In_Progress ("In Progress"),
        Downloaded("Downloaded"),
        Submitted("Submitted"),
        Completed("Completed"),
        Assigned("Assigned"),
        Location("Location");

        private String name;

        private checkinType(String s) {
            name = s;
        }
    }

    public enum assignmentType {
        Assigned ("Assigned"),
        In_Progress ("In Progress"),
        Downloaded("Downloaded"),
        Submitted("Submitted"),
        Completed("Completed");

        private String name;

        private assignmentType(String s) {
            name = s;
        }
    }

}
