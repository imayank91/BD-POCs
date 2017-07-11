package com.RareMediaCompany.BDPro.Adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.RMC.BDCloud.RealmDB.Model.StatusLog;
import com.RareMediaCompany.BDPro.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontRegular;

/**
 * Created by sidd on 11/30/16.
 */

public class RecyclerViewHistoryAdapter extends RecyclerView.Adapter<RecyclerViewHistoryAdapter.ViewHolder> {

    private RealmList<StatusLog> historyList = new RealmList<>();
    private Fragment mFragment;
    private Context mcontext;

    public RecyclerViewHistoryAdapter(Context context, Fragment fragment, RealmList<StatusLog> statusLogRealm) {
        this.historyList = statusLogRealm;
        mFragment = fragment;
        mcontext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_items_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String type = historyList.get(position).getType();

        if (position == 0) {
            holder.view1.setVisibility(View.INVISIBLE);
        }
        if (position == (historyList.size() - 1)) {
            holder.view2.setVisibility(View.INVISIBLE);
        }

        if(type.equals("Photo")){
            holder.historyType.setText("Image Captured");
            holder.fab.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.capture_timeline));
        }else if(type.equals("Signature")){
            holder.historyType.setText("Signature Captured");
            holder.fab.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.timeline_signature));
        }else if(type.equals("Call")){
            holder.historyType.setText("Outgoing Call");
            holder.fab.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.call_timeline));
        }else if(type.equals("Note")){
            holder.historyType.setText("Notes Added");
            holder.fab.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.notes_timeline));
        }

        holder.dateAndTime.setText(historyList.get(position).getTimestamp());

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.outgoing_call_tag)
        TextView historyType;
        @BindView(R.id.outgoing_call_time)
        TextView dateAndTime;
        @BindView(R.id.view_1)
        View view1;
        @BindView(R.id.view_2)
        View view2;
        @BindView(R.id.fab)
        FloatingActionButton fab;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            historyType.setTypeface(mFontRegular);
            dateAndTime.setTypeface(mFontRegular);

        }



    }
}
