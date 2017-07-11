package com.RareMediaCompany.BDPro.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;

import org.altbeacon.beacon.Beacon;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde.
 * @author Mitchell de Vries.
 */
public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconHolder> {

    /**
     * Callback which has to be implemented by the hosting activity.
     * <p/>
     * Callback interface allows for a component to be a completely self-contained,
     * modular component that defines its own layout and behaviour.
     */
    public interface OnBeaconSelectedListener {

        /**
         * Handles on beacon selected event.
         *
         * @param beacon Selected beacon.
         * @param s
         * @param s1
         */
        void onBeaconSelected(Beacon beacon, String s, String s1);
    }

    private List<Beacon> mBeacons;
    private OnBeaconSelectedListener mCallback;
    private Context mContext;

    /**
     * Creates a new Beacon adapter.
     *
     * @param beacons List of beacons.
     * @param callback OnBeaconSelected callback.
     * @param context Context.
     */
    public BeaconAdapter(List<Beacon> beacons, OnBeaconSelectedListener callback, Context context) {
        mBeacons = beacons;
        mCallback = callback;
        mContext = context;
    }

    @Override
    public BeaconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View beaconItemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_beacon, parent, false);
        return new BeaconHolder(beaconItemView);
    }

    @Override
    public void onBindViewHolder(BeaconHolder holder, int position) {
        // Binds beacon to view holder.
        holder.bindBeacon(mBeacons.get(position));
    }

    @Override
    public int getItemCount() {
        return mBeacons.size();
    }

    /**
     * View holder for list items displaying beacon data.
     */
    class BeaconHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.list_item_beacon_title)
        TextView mBeaconTitle;
        @BindView(R.id.list_item_beacon_distance)
        TextView mBeaconDistance;
        @BindView(R.id.list_item_beacon_major_minor)
        TextView mBeaconMajorMinor;

        private Beacon mBeacon;

        /**
         * Creates a new Beacon holder.
         *
         * @param itemView View holder layout.
         */
        public BeaconHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Binds beacon to view holder to display its data.
         *
         * @param beacon Beacon
         */
        public void bindBeacon(Beacon beacon) {
            String majorMinorText = "Not available";
            if (mBeacon != null && mBeacon.getId2() != null && mBeacon.getId3() != null) {
                majorMinorText = mContext.getString(R.string.list_item_major_minor,
                        mBeacon.getId2(),
                        mBeacon.getId3());
            }
            mBeacon = beacon;
            if(SingleTon.getInstance().getAddress() != null) {
                mBeaconTitle.setText(SingleTon.getInstance().getAddress());
            } else{
                mBeaconTitle.setText("Test Office");
            }

            mBeaconDistance.setText(mContext.getString(R.string.list_item_distance, String.format("%.2f", mBeacon.getDistance())));
            if(mBeacon != null ) {

                if(mBeacon.getId2().toString().equalsIgnoreCase("390")) {
                    mBeaconMajorMinor.setText("User 1");
                } else  if(mBeacon.getId2().toString().equalsIgnoreCase("391")){
                    mBeaconMajorMinor.setText("User 2");
                } else  if(mBeacon.getId2().toString().equalsIgnoreCase("392")){
                    mBeaconMajorMinor.setText("User 3");
                }else  if(mBeacon.getId2().toString().equalsIgnoreCase("393")){
                    mBeaconMajorMinor.setText("User 4");
                }else  if(mBeacon.getId2().toString().equalsIgnoreCase("394")){
                    mBeaconMajorMinor.setText("User 5");
                }else  if(mBeacon.getId2().toString().equalsIgnoreCase("395")){
                    mBeaconMajorMinor.setText("User 6");
                }
            }

        }

        @Override
        public void onClick(View v) {
            if(mBeacon.getId2().toString().equalsIgnoreCase("390")) {
                mCallback.onBeaconSelected(mBeacon,"User 1","1234567890");
            } else  if(mBeacon.getId2().toString().equalsIgnoreCase("391")){
                mCallback.onBeaconSelected(mBeacon,"User 2","1234567890");
            }else  if(mBeacon.getId2().toString().equalsIgnoreCase("392")){
                mCallback.onBeaconSelected(mBeacon,"User 3","1234567890");
            }else  if(mBeacon.getId2().toString().equalsIgnoreCase("393")){
                mCallback.onBeaconSelected(mBeacon,"User 4","1234567890");
            }else  if(mBeacon.getId2().toString().equalsIgnoreCase("394")){
                mCallback.onBeaconSelected(mBeacon,"User 5","1234567890");
            }else  if(mBeacon.getId2().toString().equalsIgnoreCase("395")){
                mCallback.onBeaconSelected(mBeacon,"User 6","1234567890");
            }else {
                mCallback.onBeaconSelected(mBeacon, "", "");
            }
        }
    }
}
