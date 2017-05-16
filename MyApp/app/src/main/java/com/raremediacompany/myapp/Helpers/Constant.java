package com.raremediacompany.myapp.Helpers;

import android.app.AlarmManager;
import android.text.TextUtils;

public class Constant {

    public static final String PREFS_NAME = "MyPrefs";
    public final static long MAX_WAKEUP_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public static String VIHAAN_SCHEMA = "myApp.realm";


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
