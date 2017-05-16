package com.RareMediaCompany.BDPro.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.RareMediaCompany.BDPro.Adapter.GooglePlacesAutocompleteAdapter;
import com.RareMediaCompany.BDPro.Helpers.GoogleGeoCoderHolder;
import com.RareMediaCompany.BDPro.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by niks on 2/3/17.
 */

public class MyCustomMapActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        TextView.OnEditorActionListener, GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String GooglePlacesAPI_KEY = "AIzaSyDiliEZ49d6O_QD-0PdByedySz92TznW6I";
    private static final int REQUEST_CAMERA = 1111;
    private static final int SELECT_FILE = 1112;

    //@BindView(R.id.autocomplete)
    private AutoCompleteTextView autoCompView;
    Double latitude, longitude;
    Context context;
    //@BindView(R.id.groupName)
    private AppCompatEditText groupNameET;

    String str = "";
    //@BindView(R.id.toolbar)
    private Toolbar toolbar;
    //@BindView(R.id.custom_map_cross)
    private ImageView searchCrossImage;
    private GoogleMap mMap;
    //private MaterialDialog materialDialog;
    private SupportMapFragment mapFragment;
    private Realm realm, realm1;
    public static String groupName;

    private ImageView profileIV;

    public AppCompatTextView centerTitleTV, newGroupTV, cancelTV;

    private String uuid;
    private Context mContext = this;
    LatLng latLng;
    @BindView(R.id.cancel_address_assignment)
    Button cancel_button;
    @BindView(R.id.save_address_assignment)
    Button save_assignment;

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + GooglePlacesAPI_KEY);
//            sb.append("&components=country:gr");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_map_layout);
       // mContext = this;
        ButterKnife.bind(this);

        autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchCrossImage = (ImageView) findViewById(R.id.custom_map_cross);


        autoCompView.setOnEditorActionListener(this);
        autoCompView.setImeActionLabel("Search", EditorInfo.IME_ACTION_UNSPECIFIED);
        profileIV = (ImageView) findViewById(R.id.profileImageId);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);

        mapFragment.getMapAsync(this);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.map_single_item));
        autoCompView.setOnItemClickListener(this);

        searchCrossImage.setOnClickListener(this);


    }

    @OnClick(R.id.cancel_address_assignment)
    public void closeActivity(){
        finish();
    }

    @OnClick(R.id.save_address_assignment)
    public void saveAddress(){


        if(autoCompView.getText().toString().isEmpty()){
            Toast.makeText(MyCustomMapActivity.this, "Enter location before saving", Toast.LENGTH_LONG ).show();
        }else {
            Intent data = new Intent();

            data.putExtra("addressInString", autoCompView.getText().toString());
            data.putExtra("lat", "" + latitude);
            data.putExtra("long", "" + longitude);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View view1 = getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
// and next place it, for exemple, on bottom right (as Google Maps app)

        str = (String) parent.getItemAtPosition(position);
        getLocationFromAddress(str);
            if (mMap == null) {
                mapFragment.getMapAsync(MyCustomMapActivity.this);
            }
//        if (mMap == null) {
//            mMap = mapFragment.getMap();
//        }
        mMap.clear();
        if (latitude != null && longitude != null) {
            //   saveButton.setVisibility(View.VISIBLE);
             latLng = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
            CameraPosition pos = CameraPosition.builder()
                    .target(latLng)
                    .zoom(12f)
                    .bearing(0.0f)
                    .tilt(0.0f)
                    .build();
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(pos), null);
            }

//            CameraUpdate center =
//                    CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12f);
//            mMap.moveCamera(center);
//            mMap.animateCamera(center);
//        mMap.animateCamera(zoom);
        }
    }

    public void getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null) {
                Address location = address.get(0);
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.custom_map_cross:
                autoCompView.setText("");
                //     saveButton.setVisibility(View.INVISIBLE);
                break;

        }

    }


    /*This will show search icon on keyboard instead enter button ( like Google maps)*/
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String location = autoCompView.getText().toString();
            if (location != null && !location.equals("")) {
                new GeocoderTask().execute(location);
            }
            return true;

        }
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Location location = mMap.getMyLocation();
        if (location != null && location.getAccuracy() < 1000) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            float acc = location.getAccuracy();
            double alt = location.getAltitude();
            new reverseGeocoderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return false;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMyLocationButtonClickListener(MyCustomMapActivity.this);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

    }


    private class updateAddressAsynctask extends AsyncTask<Object, Void, Object> {
        private Context context;

        updateAddressAsynctask(Context context) {
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] params) {

            return null;

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Time time = new Time();
            time.setToNow();

            // Fragment_Self_Assignment self_assignment = new Fragment_Self_Assignment();
            //self_assignment.updateAddress(autoCompView.getText().toString(), latitude, longitude);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

//                    if (materialDialog != null && materialDialog.isShowing()) {
//                        materialDialog.dismiss();
//                    }
                    finish();

                }
            }, 500);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            materialDialog = new MaterialDialog.Builder(getApplicationContext())
//                    .title("Updating place")
//                    .content("  Please wait...")
//                    .progress(true, 0)
//                    .cancelable(false)
//                    .show();
//
        }
    }


    public class reverseGeocoderTask extends AsyncTask<String, String, String> {
        String response = null;
        GoogleGeoCoderHolder coderHolder;
        // ChitrguptPlatformServiceAndroid platformServiceAndroid;
        String fulladdress;

        @Override
        protected String doInBackground(String... params) {
            String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true";
            try {
                response = getCurrentAddress(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                Gson gson1 = new Gson();
                coderHolder = gson1.fromJson(response, GoogleGeoCoderHolder.class);
                if (coderHolder != null) {
                    fulladdress = coderHolder.results.get(0).formatted_address;
                    fulladdress = fulladdress.replaceAll("\n", ",");
                    Log.wtf("Coder Holder", coderHolder.results.get(0).formatted_address);
                }
            }
            return fulladdress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            platformServiceAndroid = Utils
//                    .getPlatformService(getApplicationContext());
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            str = fulladdress;
            autoCompView.setText(fulladdress);
            //    saveButton.setVisibility(View.VISIBLE);

        }
    }

    private String getCurrentAddress(String urlStr) {
        String retValue = null;
        try {

            URL mUrl = new URL(urlStr);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-length", "0");
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(100000);
            httpConnection.setReadTimeout(100000);

            httpConnection.connect();

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                retValue = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return retValue;


    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view1 = getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            } else {

                // Clears all the existing markers on the map
                mMap.clear();
                // Adding Markers on Google Map for each matching address
                for (int i = 0; i < addresses.size(); i++) {

                    Address address = (Address) addresses.get(i);

                    // Creating an instance of GeoPoint, to display in Google Map
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();
                    if (latitude != null && longitude != null) {
                        //           saveButton.setVisibility(View.VISIBLE);
                    }

                    str = String.format("%s, %s",
                            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                            address.getCountryName());
                    Log.i("currentAddress", str);
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    CameraPosition pos = CameraPosition.builder()
                            .target(latLng)
                            .zoom(12f)
                            .bearing(0.0f)
                            .tilt(0.0f)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(pos), null);

                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
        if (realm1 != null) {
            realm1.close();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finish();
    }
}
