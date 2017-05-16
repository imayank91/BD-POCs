package com.RareMediaCompany.BDPro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.RMC.BDCloud.RealmDB.Model.RMCAssignmentDraft;
import com.RareMediaCompany.BDPro.Activities.AddSelfAssignmentActivity;
import com.RareMediaCompany.BDPro.Activities.AssignmentDetailActivity;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontRegular;

/**
 * Created by niks on 20/3/17.
 */

public class AssignmentDraftsRecyclerViewAdapter extends RecyclerView.Adapter<AssignmentDraftsRecyclerViewAdapter.ViewHolder> {

   private RealmResults<RMCAssignmentDraft> results;
   private Context mContext;

  public AssignmentDraftsRecyclerViewAdapter(Context context, RealmResults<RMCAssignmentDraft> rmcAssignmentDrafts){

      this.results = rmcAssignmentDrafts;
      this.mContext = context;
  }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_items_assignmentdrafts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AssignmentDraftsRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.jobNumber.setText(results.get(position).getJobNumber());
        holder.description.setText(results.get(position).getDraft_description());


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("Drafts", results.get(position));
//                Intent intent = new Intent(mContext, AddSelfAssignmentActivity.class);
//                intent.putExtra()
//                mContext.startActivity(intent);
////                mContext.startActivity(new Intent(mContext, AddSelfAssignmentActivity.class));
//                SingleTon.getInstance().setAssignmentDraftsResults(results.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.jobNumberForDrafts)
        TextView jobNumber;
        @BindView(R.id.descriptionForDrafts)
        TextView description;
        @BindView(R.id.drafts_cardview)
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            jobNumber.setTypeface(mFontRegular);
            description.setTypeface(mFontRegular);
        }


    }
}