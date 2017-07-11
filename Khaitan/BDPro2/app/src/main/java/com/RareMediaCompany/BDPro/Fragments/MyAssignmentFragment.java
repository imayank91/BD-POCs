package com.RareMediaCompany.BDPro.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.RMC.BDCloud.Android.UploadImagesTask;
import com.RareMediaCompany.BDPro.Activities.AddSelfAssignmentActivity;
import com.RareMediaCompany.BDPro.Activities.AssignmentDetailActivity;
import com.RareMediaCompany.BDPro.Activities.FilterActivity;
import com.RareMediaCompany.BDPro.Activities.MyDashboardActivity;
import com.RareMediaCompany.BDPro.Adapter.MyFragmentPagerAdapter;
import com.RareMediaCompany.BDPro.Manifest;
import com.RareMediaCompany.BDPro.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by sidd on 12/8/16.
 */

public class MyAssignmentFragment extends Fragment {

    public static ImageButton listViewButton;
    public static FrameLayout bottomNavigation;
    private final String LOG = "MyAssignmentFragment";
    @BindView(R.id.viewpager)
    public ViewPager viewPager;
    @BindView(R.id.mapview_button)
    public ImageButton mapButton;
    @BindView(R.id.filterButton)
    public ImageButton filterButton;
    @BindView(R.id.sortButton)
    public ImageButton sortButton;
    @BindView(R.id.rootlayout_activity_myassign)
    View view;
    String[] sortParams = {"Start Date - Ascending", "Start Date - Descending", "Time To Deadline"
            , "Default"};
    private MyFragmentPagerAdapter fragmentAdapter;

    SharedPreferences myFilterPreferences;
    public static final String FILTERPREFS = "filterprefs";

    MyDashboardActivity dashboardActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFilterPreferences = getActivity().getSharedPreferences(FILTERPREFS, Context.MODE_PRIVATE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myassignment, container, false);
        ButterKnife.bind(this, view);
        dashboardActivity = (MyDashboardActivity) getActivity();

        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(dashboardActivity.mSearchAction != null) {
            dashboardActivity.mSearchAction.setVisible(true);
        }
        if(dashboardActivity.mAssignmentNotif != null) {
            dashboardActivity.mAssignmentNotif.setVisible(true);
        }

        if(dashboardActivity.mNotifcations != null) {
            dashboardActivity.mNotifcations.setVisible(false);
        }

        listViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listViewButton.setImageResource(R.drawable.list_selected);
                mapButton.setImageResource(R.drawable.location);
                sortButton.setEnabled(true);
                EventBus.getDefault().post("listview");
                sortButton.setImageResource(R.drawable.sort);
            }
        });

    }

    @OnClick(R.id.mapview_button)
    public void moveToMapView() {

        listViewButton.setImageResource(R.drawable.list);
        mapButton.setImageResource(R.drawable.location_selected);
        sortButton.setEnabled(false);
        sortButton.setImageResource(R.drawable.sort_grey);
        EventBus.getDefault().post("mapview");
    }

    private void setupViewPager(ViewPager viewpager) {
        viewpager.setOffscreenPageLimit(2);

        fragmentAdapter = new MyFragmentPagerAdapter(this.getChildFragmentManager());
        fragmentAdapter.addFragment(new NewAssignmentFragment(), "New");
        fragmentAdapter.addFragment(new InProgressFragment(), "In Progress");
        fragmentAdapter.addFragment(new CompletedAssignmentFragment(), "Completed");

        viewpager.setAdapter(fragmentAdapter);
    }


    private void initView(View v) {

        listViewButton = (ImageButton) v.findViewById(R.id.listview_button);
        bottomNavigation = (FrameLayout) v.findViewById(R.id.bottomNavigation);
        setupViewPager(viewPager);
        dashboardActivity.tabLayout.setupWithViewPager(viewPager);
        dashboardActivity.tabLayout.getTabAt(0).setText("New");
        dashboardActivity.tabLayout.getTabAt(1).setText("In Progress");
        dashboardActivity.tabLayout.getTabAt(2).setText("Completed");

    }

    @OnClick(R.id.fab_add)
    public void addSelfAssignment() {
        startActivity(new Intent(getActivity(), AddSelfAssignmentActivity.class));
    }

    @OnClick(R.id.filterButton)
    public void openFilter() {
        startActivity(new Intent(getActivity(), FilterActivity.class));
    }

    @OnClick(R.id.sortButton)
    public void sort() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("SORT BY");
        builder.setSingleChoiceItems(sortParams, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    EventBus.getDefault().post("startDataAsc");
                    dialog.dismiss();
                    sortButton.setImageResource(R.drawable.sort_selected);
                    Snackbar.make(view, "Sorted By ASC Start Date", Snackbar.LENGTH_SHORT).show();
                } else if (which == 1) {
                    EventBus.getDefault().post("startDateDsc");
                    dialog.dismiss();
                    sortButton.setImageResource(R.drawable.sort_selected);
                    Snackbar.make(view, "Sorted by DSC Start Date", Snackbar.LENGTH_SHORT).show();
                } else if (which == 2) {
                    EventBus.getDefault().post("Deadline");
                    dialog.dismiss();
                    sortButton.setImageResource(R.drawable.sort_selected);
                    Snackbar.make(view, "Sorted by Time To Deadline", Snackbar.LENGTH_SHORT).show();
                } else if (which == 3) {
                    EventBus.getDefault().post("Clear sort");
                    sortButton.setImageResource(R.drawable.sort);
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "ON RESUME CALLED HERE");
        if (AssignmentDetailActivity.submitted) {
            viewPager.setCurrentItem(2);
            AssignmentDetailActivity.submitted = false;
        }

        if (AssignmentDetailActivity.movedToInProgress) {
            viewPager.setCurrentItem(1);
            AssignmentDetailActivity.movedToInProgress = false;
        }

        if (AddSelfAssignmentActivity.assignmentCreated) {
            viewPager.setCurrentItem(0);
            AddSelfAssignmentActivity.assignmentCreated = false;
        }
//
//        if (FilterActivity.filterApplied == true) {
//            filterButton.setImageResource(R.drawable.filter_selected);
//        } else {
//
//        }

        if(myFilterPreferences.contains("filterSD") || myFilterPreferences.contains("filterED")
                || myFilterPreferences.contains("filterAT")){
            filterButton.setImageResource(R.drawable.filter_selected);
        }else{
            filterButton.setImageResource(R.drawable.filter);
        }

        UploadImagesTask task = new UploadImagesTask(getActivity(), null);
        task.uploadToS3();
    }
}

