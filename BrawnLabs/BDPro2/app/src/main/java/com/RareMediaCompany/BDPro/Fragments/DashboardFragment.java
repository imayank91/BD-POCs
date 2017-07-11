package com.RareMediaCompany.BDPro.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RareMediaCompany.BDPro.R;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import io.realm.Realm;
import io.realm.RealmQuery;

import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFont;
import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontRegular;

/**
 * Created by sidd on 12/8/16.
 */

public class DashboardFragment extends Fragment {

    private DecoView arcView;
    private View view;
  //  private SharedPreferences sharedPreferences;
   // private String APP_USAGE = "appusage";
    Realm realm;
    private int totalAssignmentSize;
    private int completedAssignmentSize;
     TextView textPercentage;
    TextView label_completedAssignment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = BDCloudUtils.getRealmBDCloudInstance();


//        query = realm.where(RMCAssignment.class).beginGroup().
//                equalTo("status", "Assigned").
//                or()
//                .equalTo("status","Created").or()
//                .equalTo("status", "Completed").or()
//                .equalTo("status", "In Progress")
//                .endGroup();
//
//         totalAssignmentSize = query.findAll().size();
//
        totalAssignmentSize = realm.where(RMCAssignment.class).findAll().size();
      completedAssignmentSize = realm.where(RMCAssignment.class)
              .equalTo("status", "Completed").findAll().size();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        arcView = (DecoView)view.findViewById(R.id.dynamicArcView);



      //  sharedPreferences = getContext(). getSharedPreferences(APP_USAGE, Context.MODE_PRIVATE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(totalAssignmentSize == 0){
            totalAssignmentSize =1;
        }

        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, totalAssignmentSize, totalAssignmentSize).setSpinDuration(101)
                .build();

        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FF4C4C"))
                .setRange(0, totalAssignmentSize, 0).setSpinDuration(1000)
                .build();

        int backIndex = arcView.addSeries(seriesItem);

        arcView.addEvent(new DecoEvent.Builder(50)
                .setIndex(backIndex)
                .build());


        int series1Index = arcView.addSeries(seriesItem1);

         textPercentage = (TextView)view.findViewById(R.id.textPercentage);
        textPercentage.setTypeface(mFontRegular);

        label_completedAssignment = (TextView)view.findViewById(R.id.label_completed_assignment);
        label_completedAssignment.setTypeface(mFontRegular);

        double data = ((double)completedAssignmentSize)/((double)totalAssignmentSize);

        String value = String.format("%.1f", data*100);

        textPercentage.setText(value+"%");
//        seriesItem1.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
//            @Override
//            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                float percentFilled = ((currentPosition - seriesItem1.getMinValue()) / (seriesItem1.getMaxValue() - seriesItem1.getMinValue()));
//
//
//                //     textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
//
//                textPercentage.setText(String.valueOf((1/totalAssignmentSize)*100)+"%");
////                  textPercentage.setText(String.valueOf(10));
//
//             }
//
//            @Override
//            public void onSeriesItemDisplayProgress(float percentComplete) {
//
//            }
//        });


        arcView.addEvent(new DecoEvent.Builder(completedAssignmentSize)
                .setIndex(series1Index)
                .setDelay(50)
                .build());
    }
}
