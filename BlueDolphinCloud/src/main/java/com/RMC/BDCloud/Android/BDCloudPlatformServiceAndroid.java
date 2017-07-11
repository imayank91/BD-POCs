package com.RMC.BDCloud.Android;

import android.content.Context;
import android.util.Log;

import com.RMC.BDCloud.Core.BDCloudPlatformService;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.RMC.BDCloud.Android.BDCloudUtils.DEBUG_LOG;
import static com.RMC.BDCloud.Android.BDCloudUtils.ERROR_LOG;
import static com.RMC.BDCloud.Android.BDCloudUtils.INFO_LOG;

/**
 * Created by mayanksaini on 06/10/16.
 */

public class BDCloudPlatformServiceAndroid implements BDCloudPlatformService {

    private RequestCallback requestCallback;
    private static RequestQueue getQueue, postQueue, putQueue;


    public BDCloudPlatformServiceAndroid() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init(Object context, RequestCallback requestCallback) {
        // TODO Auto-generated method stub
        BDCloudUtils.setContext((Context) context);
        this.requestCallback = requestCallback;
    }

    @Override
    public void executeHttpGet(String url, Context context, final int type, final JSONObject headers) throws Exception {
        final String[] returnResponse = {null};

        Log.i("BDCloudPLATFORMService "+url,"");


        if(getQueue == null) {
            getQueue = Volley.newRequestQueue(context);
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                returnResponse[0] = response.toString();
                requestCallback.onResponseReceived(response, true, null, type);
                if (DEBUG_LOG == true) {
                    Log.d("executeHttpGet--response", returnResponse[0].toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                returnResponse[0] = null;
                requestCallback.onResponseReceived(null, false, null, type);
                if (ERROR_LOG == true) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        Log.e("executeHttpGet-Status code", String.valueOf(networkResponse.statusCode));
                        try {
                            Log.e("executeHttpGet-Exception message", error.getMessage());
                        }catch (Exception e){

                        }
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Accept-Encoding", "application/json");

                if (headers != null) {
                    String authorization = null;
                    String userId = null;
                    try {
                        authorization = headers.getString("authorization");
                        userId = headers.getString("userId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(userId != null){
                        params.put("userId", userId);
                    }

                    if(authorization != null) {
                        params.put("Authorization", "Bearer " + authorization);
                    }
                }
                //..add other headers
                return params;
            }
        };

        getQueue.add(jsonObjReq);

    }

    @Override
    public void executeHttpPost(String url, JSONObject postString, Context context, final int type, final JSONObject headers, final Map<String, String> paramsMap) throws Exception {
        Log.i("Put Payload",""+postString);
        final String[] returnResponse = {null};

        if(postQueue == null) {
            postQueue = Volley.newRequestQueue(context);
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url,
                postString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                returnResponse[0] = response.toString();
                requestCallback.onResponseReceived(response, true, null, type);
                if (DEBUG_LOG == true) {
                    Log.d("executeHttpPost--response", returnResponse[0].toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                returnResponse[0] = null;

                if (ERROR_LOG == true) {
                    NetworkResponse networkResponse = error.networkResponse;
                    try {
                        requestCallback.onResponseReceived(null, false, "Failed", type);
                        String body = new String(error.networkResponse.data, "UTF-8");
                        JSONObject obj = new JSONObject(body);
                        Log.e("executeHttpPost-Status data", body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    if (networkResponse != null) {
                        Log.e("executeHttpPost-Status code", String.valueOf(networkResponse.statusCode));
                        error.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Accept-Encoding", "application/json");

                if (headers != null) {
                    String authorization = null;
                    String userId = null;
                    try {
                        authorization = headers.getString("authorization");
                        userId = headers.getString("userId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(userId != null){
                        params.put("userId", userId);
                    }

                    if(authorization != null) {
                        params.put("Authorization", "Bearer " + authorization);
                    }
                }

                //..add other headers
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                return paramsMap;
            }
        };

        //Configuring volley to stop sending two request to the server
        // Time stoppage - 60 * 1000 = 60 seconds
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 0,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        postQueue.add(jsonObjReq);

    }


    @Override
    public void executeHttpPut(String url, JSONObject putString, Context context, final int type, final JSONObject headers, final Map<String, String> paramsMap) throws Exception {
        Log.i("pUT", ""+putString);
        final String[] returnResponse = {null};
        if(putQueue == null) {
            putQueue = Volley.newRequestQueue(context);
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url,
                putString, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                returnResponse[0] = response.toString();
                requestCallback.onResponseReceived(response, true, null, type);
                if (DEBUG_LOG == true) {
                    Log.d("executeHttpPut--response", returnResponse[0].toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                returnResponse[0] = null;

                if (ERROR_LOG == true) {
                    NetworkResponse networkResponse = error.networkResponse;
                    requestCallback.onResponseReceived(null, false, null, type);
                    if (networkResponse != null) {
                        Log.e("executeHttpPut-Status code", String.valueOf(networkResponse.statusCode));
                        try {
                            String body = new String(error.networkResponse.data, "UTF-8");
                            JSONObject obj = new JSONObject(body);
                            requestCallback.onResponseReceived(obj, false, body, type);
                            Log.e("executeHttpPost-Status data", body);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Accept-Encoding", "application/json");

                String authorization = null;
                String userId = null;
                try {
                    authorization = headers.getString("authorization");
                    userId = headers.getString("userId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (authorization != null) {
                    params.put("Authorization", "Bearer " + authorization);
                }

                if (userId != null) {
                    params.put("userId", userId);
                }

                //..add other headers
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }

        };
        putQueue.add(jsonObjReq);

    }
}
