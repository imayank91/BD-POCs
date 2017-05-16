package com.RMC.BDCloud.Sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * Created by mayanksaini on 23/11/16.
 */

public class BDCloudAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private BDCloudAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new BDCloudAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
