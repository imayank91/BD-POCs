package com.RareMediaCompany.BDPro.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by niks on 21/3/17.
 */

public class BeaconListRecyclerViewAdapter extends RecyclerView.Adapter<BeaconListRecyclerViewAdapter.MyViewHolder> {


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView titleTV, title1TV, title2TV, title3TV, title4TV;
        public LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
