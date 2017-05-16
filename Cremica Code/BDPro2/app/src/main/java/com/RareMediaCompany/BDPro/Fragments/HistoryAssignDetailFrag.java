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
import com.RMC.BDCloud.RealmDB.Model.StatusLog;
import com.RareMediaCompany.BDPro.Adapter.RecyclerViewHistoryAdapter;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by sidd on 11/29/16.
 */

public class HistoryAssignDetailFrag extends Fragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private RecyclerViewHistoryAdapter recyclerviewAdapter;
    private RealmList<StatusLog> statusLogList;
    private String activeAssignmentID;
    private RMCAssignment rmcAssignment;
    private Realm realm;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = BDCloudUtils.getRealmBDCloudInstance();
     //   statusLogList = new ArrayList<>();
        activeAssignmentID = SingleTon.getInstance().getAssignmentModel().assignmentId;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_history_asgndetails, container, false);
        ButterKnife.bind(this, view);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        rmcAssignment = realm.where(RMCAssignment.class).equalTo("assignmentId", activeAssignmentID).findFirst();

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        statusLogList = rmcAssignment.getStatusLog();

        recyclerviewAdapter = new RecyclerViewHistoryAdapter(getContext(), HistoryAssignDetailFrag.this, statusLogList);
        recyclerView.setAdapter(recyclerviewAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
