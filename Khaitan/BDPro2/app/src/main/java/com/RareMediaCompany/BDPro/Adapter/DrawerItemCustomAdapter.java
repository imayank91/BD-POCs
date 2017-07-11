package com.RareMediaCompany.BDPro.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.RareMediaCompany.BDPro.R;

import java.util.ArrayList;

/**
 * Created by sidd on 12/7/16.
 */

public class DrawerItemCustomAdapter extends ArrayAdapter<String> {

    Context mContext;
    int layoutResourceId;
    ArrayList<String> items;

    public DrawerItemCustomAdapter(Context context, int resource, ArrayList<String> drawerItems) {
        super(context, resource, drawerItems);

        this.mContext = context;
        this.items = drawerItems;
        this.layoutResourceId = resource;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        view = inflater.inflate(layoutResourceId, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.textViewName);
        textView.setText(items.get(position));
        return view;
    }


}
