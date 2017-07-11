package com.RMC.BDCloud.Android;

import org.json.JSONObject;

/**
 * Created by Mayank on 17/10/16.
 */
public interface RequestCallback {

    public void onResponseReceived(JSONObject object, boolean status, String message, int type);

}
