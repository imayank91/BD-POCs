package com.RareMediaCompany.BDPro.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.RareMediaCompany.BDPro.Activities.MyDashboardActivity;

/**
 * Created by sidd on 12/7/16.
 */

public class CallHistoryFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyDashboardActivity dashboardActivity = (MyDashboardActivity) getActivity();
        dashboardActivity.mSearchAction.setVisible(false);
        dashboardActivity.mAssignmentNotif.setVisible(false);
        dashboardActivity.mNotifcations.setVisible(true);


    }
}
