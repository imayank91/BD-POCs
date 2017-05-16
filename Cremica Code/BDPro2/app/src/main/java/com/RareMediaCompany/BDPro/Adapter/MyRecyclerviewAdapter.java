package com.RareMediaCompany.BDPro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.RareMediaCompany.BDPro.Activities.AssignmentDetailActivity;
import com.RareMediaCompany.BDPro.DataModels.AssignmentModel;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontBold;
import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontRegular;


/**
 * Created by sidd on 11/24/16.
 */

public class MyRecyclerviewAdapter extends RecyclerView.Adapter<MyRecyclerviewAdapter.ViewHolder> {


    private ArrayList<AssignmentModel> tag = new ArrayList<>();
    private Fragment mFragment;
    private Context mcontext;

//    private MyFilter myFilter;

    public MyRecyclerviewAdapter(Context context, Fragment fragment, ArrayList<AssignmentModel> tag) {
        this.tag = tag;
        mFragment = fragment;
        mcontext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_items_newassignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.address.setText(tag.get(position).address);
        holder.start_date.setText(tag.get(position).startDate);
        holder.end_date.setText(tag.get(position).endDate);
        holder.assignmentJobNumber.setText(tag.get(position).jobNumber);

        holder.assignmentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcontext.startActivity(new Intent(mcontext, AssignmentDetailActivity.class));
                SingleTon.getInstance().setAssignmentModel(tag.get(position));
            }
        });

        Log.i("Checking TRUE OR FALSE",""+tag.get(position).isOpenedInApp);

        if(tag.get(position).isOpenedInApp){
            holder.isOpened.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return tag.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.assignment_tag)
        TextView assignmentJobNumber;

        @BindView(R.id.label_start_date)
        TextView startDateLabel;

        @BindView(R.id.start_date)
        TextView start_date;

        @BindView(R.id.label_end_date)
        TextView endDateLabel;

        @BindView(R.id.end_date)
        TextView end_date;

        @BindView(R.id.address)
        TextView address;

        @BindView(R.id.cardview_assignment)
        CardView assignmentDetails;

        @BindView(R.id.is_opened)
        ImageView isOpened;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            assignmentJobNumber.setTypeface(mFontBold);
            startDateLabel.setTypeface(mFontRegular);
            start_date.setTypeface(mFontRegular);
            endDateLabel.setTypeface(mFontRegular);
            end_date.setTypeface(mFontRegular);
            address.setTypeface(mFontRegular);
        }


    }


}
