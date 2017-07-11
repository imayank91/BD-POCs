package com.raremediacompany.myapp.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.RMC.BDCloud.BLE.BeaconScannerService;
import com.RMC.BDCloud.Sync.BDCloudSyncAdapter;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by mayanksaini on 31/05/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));

        int hourofday = cal.get(Calendar.HOUR_OF_DAY);

        if (hourofday > 7 && hourofday < 21) {
            if (!BDCloudSyncAdapter.isMyServiceRunning(BeaconScannerService.class, context)) {
                Log.i("AlarmReceiver", "BeaconScannerService");
                context.startService(new Intent(context, BeaconScannerService.class));
            }
        }

    }
}
