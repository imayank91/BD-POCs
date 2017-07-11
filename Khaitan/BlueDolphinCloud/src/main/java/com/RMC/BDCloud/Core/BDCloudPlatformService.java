package com.RMC.BDCloud.Core;

import android.content.Context;

import com.RMC.BDCloud.Android.RequestCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by mayanksaini on 06/10/16.
 */

public interface BDCloudPlatformService {

    public void init(Object object, RequestCallback requestCallback);

    public void executeHttpGet(String url, Context context, final int type, final JSONObject headers) throws Exception;

    public void executeHttpPost(String url, JSONObject postString,Context context, final int type, JSONObject headers, Map<String,String> paramsMap) throws Exception;

    public void executeHttpPut(String url, JSONObject putString, Context context, final int type, JSONObject headers, Map<String,String> paramsMap) throws Exception;

}
