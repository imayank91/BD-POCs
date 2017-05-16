package com.RareMediaCompany.BDPro.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCLocation;
import com.RareMediaCompany.BDPro.Adapter.MyRecyclerviewAdapter;
import com.RareMediaCompany.BDPro.DataModels.AssignmentModel;
import com.RareMediaCompany.BDPro.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by sidd on 11/23/16.
 */

public class InProgressFragment extends Fragment {


    private View view;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.mapview)
    RelativeLayout mapLayout;
    boolean mapReady = false;
    private SupportMapFragment map;
    private List<Marker> markersList;
    private LatLngBounds.Builder builder;
    private CameraUpdate cu;
    private AssignmentModel assignmentModel;
    private ArrayList<AssignmentModel> assignmentModelList;
    private RealmResults<RMCAssignment> result;
    private MyRecyclerviewAdapter recyclerviewAdapter;
    private GoogleMap mGoogleMap;
    private Realm realm;
    @BindView(R.id.window)
    CardView window;
    private String _sortUsing;
    private SharedPreferences filterPrefs;
    public static final String FILTERPREFS = "filterprefs";
    @BindView(R.id.jobNumberForMapView)
    TextView jobNumberForMapView;
    @BindView(R.id.addressForMapView)
    TextView addressForMapView;
    @BindView(R.id.startDateForMapView)
    TextView startDateForMapView;
    @BindView(R.id.endDateForMapView)
    TextView endDateForMapView;
    private Date fromStartDate = null;
    private Date toStartDate = null;
    private Date fromEndDate = null;
    private Date toEndDate = null;
    private RealmQuery<RMCAssignment> query;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = BDCloudUtils.getRealmBDCloudInstance();
        markersList = new ArrayList<>();
        filterPrefs = getContext().getSharedPreferences(FILTERPREFS, Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_newassignment, container, false);
        ButterKnife.bind(this, view);
        map = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Subscribe
    public void sort(String sortUsing) {

        if (sortUsing.contentEquals("startDataAsc") || sortUsing.contentEquals("startDateDsc")
                || sortUsing.contentEquals("assignmentDeadline")) {
            _sortUsing = sortUsing;
            onResume();
        }
        if (sortUsing.contentEquals("Clear sort")) {
            _sortUsing = null;
            onResume();
        }
    }

    @Override
    public void onResume() {

        assignmentModelList = new ArrayList<>();

        recyclerView.setAdapter(recyclerviewAdapter);

        query = realm.where(RMCAssignment.class).equalTo("status", "In Progress");

        filteredRealmQuery();

        /*
         * Sorting the realm results
         */
        if (_sortUsing != null) {

            if (_sortUsing.contentEquals("startDataAsc")) {
                result = query.findAll().sort("assignmentStartTime", Sort.ASCENDING);
            } else if (_sortUsing.contentEquals("startDateDsc")) {
                result = query.findAll().sort("assignmentStartTime", Sort.DESCENDING);
            } else if (_sortUsing.contentEquals("Deadline")) {
                result = query.findAll().sort("assignmentDeadline", Sort.ASCENDING);
            }
        } else {
            result = query.findAll();
        }



        for (RMCAssignment ass : result) {

            assignmentModel = new AssignmentModel();
            assignmentModel.assignmentId = ass.getAssignmentId();
            assignmentModel.address = ass.getAddress();
            assignmentModel.isOpenedInApp = ass.isOpened();

            SimpleDateFormat myformatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            Date startDate = ass.getAssignmentStartTime();
            Date endDate = ass.getAssignmentDeadline();
            System.out.println(startDate);

            if (startDate != null && endDate != null) {

                assignmentModel.startDate = myformatter.format(startDate);
                assignmentModel.endDate = myformatter.format(endDate);
            }

            String assDetails = ass.getAssignmentDetails();

            try {
                JSONObject jsonObject = new JSONObject(assDetails);

                if (jsonObject.has("notes")) {
                    assignmentModel.notes = jsonObject.getString("notes");
                }
                assignmentModel.email = jsonObject.getString("email");
                assignmentModel.contactPersonName = jsonObject.getString("contactPerson");
                assignmentModel.mobileNum = jsonObject.getString("mobile");
                Log.i("ASSIGNMENT JOB NUMBER"," "+jsonObject.getString("jobNumber"));
                assignmentModel.jobNumber=jsonObject.getString("jobNumber");
                assignmentModel.instructions = jsonObject.getString("instructions");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            assignmentModelList.add(assignmentModel);
        }

        recyclerviewAdapter = new MyRecyclerviewAdapter(getContext(), InProgressFragment.this, assignmentModelList);
        recyclerView.setAdapter(recyclerviewAdapter);

        if (mapReady) {

            Log.i(" Hey ", " Checking the function for MAP in ONRESUME");
            whenMapIsReady(mGoogleMap);
        }

        super.onResume();
    }

    @Subscribe
    public void onEvent(String view) {
        if (view.equals("mapview")) {

            recyclerView.setVisibility(View.GONE);
            mapLayout.setVisibility(View.VISIBLE);

            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mapReady = true;
                    mGoogleMap = googleMap;
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    whenMapIsReady(mGoogleMap);
                }
            });


        }else if(view.equals("listview"))
        {
            recyclerView.setVisibility(View.VISIBLE);
            mapLayout.setVisibility(View.GONE);
        }
    }

    private void whenMapIsReady(GoogleMap mGoogleMap) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.clear();
        setupMarkers();
        if (cu != null) {
            mGoogleMap.animateCamera(cu, 3000, null);
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                openInfoWindow();
                jobNumberForMapView.setText(marker.getTitle());

                for (AssignmentModel model : assignmentModelList) {
                    if (model.jobNumber.equals(marker.getTitle())) {
                        addressForMapView.setText(model.address);
                        startDateForMapView.setText(model.startDate);
                        endDateForMapView.setText(model.endDate);
                    }
                }
                return true;
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                closeInfoWindow();
            }
        });
    }

    private void openInfoWindow() {

        Animation slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
        if(window.getVisibility()==View.GONE){
            window.startAnimation(slideUp);
            window.setVisibility(View.VISIBLE);
        }
    }

    private void closeInfoWindow(){
        Animation slideDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
        if(window.getVisibility()==View.VISIBLE) {
            window.startAnimation(slideDown);
            window.setVisibility(View.GONE);
        }
    }
    private void setupMarkers() {

        for (int i = 0; i < result.size(); i++) {
            RMCAssignment ass = result.get(i);
            RMCLocation location = ass.getLocation();
            Log.i("Location : ", " Lat : " + location.getLatitude() + " Lng " + location.getLongitude());


            markersList.add(mGoogleMap.addMarker(new MarkerOptions()
                    .title(assignmentModelList.get(i).jobNumber)
                    .position(new LatLng(Double.valueOf(location.getLatitude()),
                            Double.valueOf(location.getLongitude()))))
            );

            builder = new LatLngBounds.Builder();

            for (Marker m : markersList) {
                builder.include(m.getPosition());
            }

            int padding = 150;
            /**create the bounds from latlngBuilder to set into map camera*/
            LatLngBounds bounds = builder.build();
            /**create the camera with bounds and padding to set into map*/
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        }
    }

    public void filteredRealmQuery() {

        /*
         * Filtering the realm results for Start Date
         */
        if (filterPrefs.getBoolean("filterSD", false)) {

            SimpleDateFormat mydateformatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            try {
                fromStartDate = mydateformatter.parse(filterPrefs.getString("fromStartDate", ""));
                toStartDate = mydateformatter.parse(filterPrefs.getString("toStartDate", ""));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            result = query.between("assignmentStartTime", fromStartDate, toStartDate).findAll();
        }

        /*
         * Filtering the realm results for End Date
         */
        if (filterPrefs.getBoolean("filterED", false)) {

            SimpleDateFormat mydateformatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            try {
                fromEndDate = mydateformatter.parse(filterPrefs.getString("fromEndDate", ""));
                toEndDate = mydateformatter.parse(filterPrefs.getString("toEndDate", ""));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            result = query.between("assignmentDeadline", fromEndDate, toEndDate).findAll();
        }

  /*
         * Filtering the realm results for Assignment Type
         */
        if (filterPrefs.getBoolean("filterAT", false)) {
            if (filterPrefs.getString("Self", "").contentEquals("Self")) {
                Log.i("ASSIGNMENT TYPE", " SELF ASSIGNMENT");
                result = query.equalTo("assignmentType", "Self").findAll();
            } else if (filterPrefs.getString("Manager", "").contentEquals("Manager")) {
                Log.i("ASSIGNMENT TYPE", " MANAGER ASSIGNMENT");
                result = query.equalTo("assignmentType", "Manager").findAll();
            }
        }


    }


    @Subscribe
    public void performSearch(String query){
        if(!query.contains("listview") && !query.contains("mapview") && !query.contains("startDataAsc") &&
                !query.contains("startDateDsc") && !query.contains("Deadline") && !query.contains("Clear sort")) {

            final ArrayList<AssignmentModel> filteredList = new ArrayList<>();

            for (int i = 0; i < assignmentModelList.size(); i++) {

                final String text = assignmentModelList.get(i).address.toLowerCase();
                if (text.contains(query.toLowerCase())) {
                    filteredList.add(assignmentModelList.get(i));
                }
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerviewAdapter = new MyRecyclerviewAdapter(getContext(), InProgressFragment.this, filteredList);
            recyclerView.setAdapter(recyclerviewAdapter);
            recyclerviewAdapter.notifyDataSetChanged();
        }
     }

}
