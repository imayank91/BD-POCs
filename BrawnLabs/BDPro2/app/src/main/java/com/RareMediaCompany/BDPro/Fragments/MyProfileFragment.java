package com.RareMediaCompany.BDPro.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RareMediaCompany.BDPro.Activities.MyDashboardActivity;
import com.RareMediaCompany.BDPro.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by sidd on 12/7/16.
 */

public class MyProfileFragment extends Fragment {

    @BindView(R.id.profileImageId)
    CircleImageView profileIV;

    RMCUser rmcUser;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.mobileNum)
    TextView mobNum;
    @BindView(R.id.orgText)
    TextView orgName;
    @BindView(R.id.code_name)
    TextView feCode;
    @BindView(R.id.status_spinner)
    Spinner spinner;
    private Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myprofie, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        MyDashboardActivity dashboardActivity = (MyDashboardActivity) getActivity();
        dashboardActivity.mSearchAction.setVisible(false);
        dashboardActivity.mAssignmentNotif.setVisible(false);
        dashboardActivity.mNotifcations.setVisible(true);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.status_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        realm = BDCloudUtils.getRealmBDCloudInstance();

        RealmQuery<RMCUser> query = realm.where(RMCUser.class);
        rmcUser = query.findFirst();

        userName.setText(rmcUser.getUserName());
        mobNum.setText(rmcUser.getMobileNo());
        orgName.setText(rmcUser.getOrgName());
        feCode.setText(rmcUser.getMobileNo());
        realm.close();
    }
}
