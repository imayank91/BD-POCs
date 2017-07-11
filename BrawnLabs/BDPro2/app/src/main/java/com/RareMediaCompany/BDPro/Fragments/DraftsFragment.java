package com.RareMediaCompany.BDPro.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignmentDraft;
import com.RareMediaCompany.BDPro.Activities.MyDashboardActivity;
import com.RareMediaCompany.BDPro.Adapter.AssignmentDraftsRecyclerViewAdapter;
import com.RareMediaCompany.BDPro.Adapter.MyRecyclerviewAdapter;
import com.RareMediaCompany.BDPro.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by niks on 20/3/17.
 */

public class DraftsFragment extends Fragment {

    @BindView(R.id.recyclerview_assignmentDrafts)
    RecyclerView recyclerView;
    RealmResults<RMCAssignmentDraft> results;
    Realm realm;
    AssignmentDraftsRecyclerViewAdapter recyclerviewAdapter;
    RealmQuery<RMCAssignmentDraft> query;
    MyDashboardActivity dashboardActivity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = BDCloudUtils.getRealmBDCloudInstance();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_assignmentdrafts, container, false);
        ButterKnife.bind(this, view);
        dashboardActivity = (MyDashboardActivity) getActivity();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dashboardActivity.mSearchAction.setVisible(false);
        dashboardActivity.mNotifcations.setVisible(true);
        dashboardActivity.mAssignmentNotif.setVisible(false);


        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        query = realm.where(RMCAssignmentDraft.class);
        results = query.findAll();
        recyclerviewAdapter = new AssignmentDraftsRecyclerViewAdapter(getContext(), results);
        recyclerView.setAdapter(recyclerviewAdapter);


    }




}
