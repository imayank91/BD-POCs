package com.RareMediaCompany.BDPro.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.RMC.BDCloud.Android.BDCloudUtils;
import com.RMC.BDCloud.Android.BDPreferences;
import com.RMC.BDCloud.Android.Constants;
import com.RMC.BDCloud.Android.DownloadPlacesTask;
import com.RMC.BDCloud.Android.RequestCallback;
import com.RMC.BDCloud.BLE.Logger;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignee;
import com.RMC.BDCloud.RealmDB.Model.RMCBeacon;
import com.RMC.BDCloud.RealmDB.Model.RMCUser;
import com.RareMediaCompany.BDPro.Adapter.BeaconAdapter;
import com.RareMediaCompany.BDPro.Adapter.DrawerItemCustomAdapter;
import com.RareMediaCompany.BDPro.Fragments.BeaconScannerFragment;
import com.RareMediaCompany.BDPro.Fragments.CallHistoryFragment;
import com.RareMediaCompany.BDPro.Fragments.DashboardFragment;
import com.RareMediaCompany.BDPro.Fragments.DraftsFragment;
import com.RareMediaCompany.BDPro.Fragments.GalleryFragment;
import com.RareMediaCompany.BDPro.Fragments.MyAssignmentFragment;
import com.RareMediaCompany.BDPro.Fragments.MyCalendarFragment;
import com.RareMediaCompany.BDPro.Fragments.MyProfileFragment;
import com.RareMediaCompany.BDPro.Helpers.SingleTon;
import com.RareMediaCompany.BDPro.R;
import com.crashlytics.android.Crashlytics;

import org.altbeacon.beacon.Beacon;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.RareMediaCompany.BDPro.Activities.BaseActivity.mFontRegular;

/**
 * Created by sidd on 12/7/16.
 */

public class MyDashboardActivity extends AppCompatActivity implements RequestCallback, BeaconAdapter.OnBeaconSelectedListener {

    //  @BindView(R.id.tabLayout)
    public TabLayout tabLayout;
    public static MenuItem mNotifcations;
    public MenuItem mSearchAction;
    public MenuItem mAssignmentNotif;
    //    @BindView(R.id.edtSearch)
    public EditText edtSeach;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ArrayList<String> drawerItem;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawerList)
    ListView mDrawerList;
    RMCUser rmcUser;
    @BindView(R.id.lblUsername)
    TextView username;
    private boolean isSearchOpened = false;
    private Realm realm;
    InputMethodManager imm;
    String notifOnMyassignment = null;
    @BindView(R.id.versionName)
    TextView appVersion;
    int i = 0;
    private AlertDialog alertDialog;
    //Temporary
    SharedPreferences sharedPreferences;
    private String APP_USAGE = "appusage";
    private static final String DIALOG_BEACON = "dialog_beacon";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydashboard);
        BDCloudUtils.startSync(getApplicationContext());
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        //BDCloudUtils.initBaseAppClass(this);
        setupToolbar();
        realm = BDCloudUtils.getRealmBDCloudInstance();
        RealmResults<RMCAssignee> rmcAssignee = realm.where(RMCAssignee.class).findAll();
        BDPreferences preferences = new BDPreferences(getApplicationContext());
        preferences.setOrganisationId(getResources().getString(R.string.orgId));
//        DownloadPlacesTask task = new DownloadPlacesTask(getApplicationContext(),null,"");
//        task.downloadPlaces();

        Log.i("Realm Results ", "Counts " + rmcAssignee.size());

        drawerItem = new ArrayList<>();

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new DashboardFragment())
                    .commit();
            mDrawerLayout.closeDrawers();
        }

        notifOnMyassignment = getIntent().getStringExtra("MyAssignment");

        Log.i("GOT ", "NOTIFICATION STRING " + notifOnMyassignment);

        drawerItem.add("My Dashboard");
        drawerItem.add("Assignments");
//        drawerItem.add("Drafts");
//        drawerItem.add("Scan");
        //drawerItem.add("Calendar");
        //  drawerItem.add("Gallery");
        //  drawerItem.add("Call History");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.drawer_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setupDrawerToggle();

        realm = BDCloudUtils.getRealmBDCloudInstance();
        RealmQuery<RMCUser> query = realm.where(RMCUser.class);
        rmcUser = query.findFirst();
        username.setText(rmcUser.getUserName());
        toolbar_title.setTypeface(mFontRegular);

        appVersion.setText("v" + BDCloudUtils.getPackageInfo(BDCloudUtils.setContext(null)).versionName);
        sharedPreferences = getSharedPreferences(APP_USAGE, Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        BDCloudUtils.checkNetworkTime(this);

        if (sharedPreferences.getBoolean("firstrun", true)) {

            i = 1;
            sharedPreferences.edit().putBoolean("firstrun", false).putInt("app_usage_count", i).apply();
        } else {
            int j = sharedPreferences.getInt("app_usage_count", 0);
            j++;

            sharedPreferences.edit().putInt("app_usage_count", j).apply();

        }

    }

    @OnClick(R.id.profile_navigationDrawer)
    public void showProfile() {

        MyProfileFragment fragment = new MyProfileFragment();
        tabLayout.setVisibility(View.GONE);
        toolbar_title.setText("My Profile");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        mDrawerLayout.closeDrawers();
    }

    void setupToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void selectItem(final int position) {

        Fragment fragment = null;

        switch (position) {

            case 0:
                tabLayout.setVisibility(View.GONE);
                toolbar_title.setText("My Dashboard");
                mSearchAction.setVisible(false);
                mAssignmentNotif.setVisible(false);
                mNotifcations.setVisible(true);
                fragment = new DashboardFragment();
                break;

            case 1:

                tabLayout.setVisibility(View.VISIBLE);

                fragment = new MyAssignmentFragment();
                Log.i(" Fragment ", " name " + fragment);
                toolbar_title.setText("My Assignments");
                break;

            case 2:

                tabLayout.setVisibility(View.GONE);
                toolbar_title.setText("Drafts");
                fragment = new DraftsFragment();
                break;

            case 3:

                tabLayout.setVisibility(View.GONE);
                toolbar_title.setText("Scan");
                fragment = new BeaconScannerFragment();
                break;

            default:
                tabLayout.setVisibility(View.GONE);
                break;
        }

        if (fragment != null) {

            final Fragment finalFragment = fragment;
            Log.i(" Changed Fragment ", "is " + finalFragment);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, finalFragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mDrawerLayout.closeDrawers();
                }
            }, 50);

            //  setTitle(mNavigationDrawerItemTitles[position]);
            //   mDrawerLayout.closeDrawer();

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_myassignments, menu);
        mSearchAction = menu.findItem(R.id.action_search);
        mAssignmentNotif = menu.findItem(R.id.action_notification_assignments);
        mNotifcations = menu.findItem(R.id.action_notification);

        Log.i("Hey", "onCreateOptionMenu");
        if (notifOnMyassignment != null) {
            Log.i("Notification", "Received");

            if (notifOnMyassignment.equals("openMyAssignmentFragment")) {
                selectItem(1);
            }
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Hey", "onStart");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_notification:
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

    }

    void setupDrawerToggle() {
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
        };
        mDrawerToggle.syncState();
    }

    private void handleMenuSearch() {

        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            //  action.setDisplayShowTitleEnabled(true); //show the title in the action bar
            toolbar_title.setVisibility(View.VISIBLE);
            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);
            MyAssignmentFragment.bottomNavigation.setVisibility(View.VISIBLE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();

            //add the search icon in the action bar
            mSearchAction.setIcon(R.drawable.search);
            //     mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_open_search));
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            isSearchOpened = false;
        } else { //open the search entry

            toolbar_title.setVisibility(View.GONE);
            MyAssignmentFragment.bottomNavigation.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.syncState();

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        //     doSearch();
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            edtSeach.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    EventBus.getDefault().post(edtSeach.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            //open the keyboard focused in the edtSearch
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            //    mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_search));
            mSearchAction.setIcon(R.drawable.clear);
            isSearchOpened = true;
        }

    }

    @Override
    public void onResponseReceived(JSONObject object, boolean status, String message, int type) {

        if (type == Constants.pushNotificationCallback) {
            if (status) {
                Log.i("NOTIFICATION ", " IN HOME ACTIVITY");
                BDCloudUtils.sendNotification(message, this, MyDashboardActivity.class, 1,null);
            }
        }
    }


    public void mayBeCalledPausedDailog(final Beacon beacon) {


        RMCBeacon rmcBeacon = realm.where(RMCBeacon.class).beginGroup()
                .equalTo("uuid", beacon.getId1().toString())
                .equalTo("major", beacon.getId2().toString())
                .equalTo("minor", beacon.getId3().toString())
                .endGroup()
                .findFirst();

        AlertDialog.Builder builder = new AlertDialog.Builder(MyDashboardActivity.this);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflator = inflater.inflate(R.layout.paused_dialog_box_layout, null);

        builder.setView(inflator);
        builder.setCancelable(false);

        final Spinner statusSpinner = (Spinner) inflator.findViewById(R.id.reasonSpinner_id);

        if (rmcBeacon != null && rmcBeacon.getStatus() != null && rmcBeacon.getStatus().equalsIgnoreCase("I am in")) {
            statusSpinner.setSelection(0);
        } else if (rmcBeacon != null && rmcBeacon.getStatus() != null && rmcBeacon.getStatus().equalsIgnoreCase("I am out")) {
            statusSpinner.setSelection(1);
        }

        Button cancelBtn = (Button) inflator.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(onCancelButtonClicked);
        Button okBtn = (Button) inflator.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Logger(beacon, statusSpinner.getSelectedItem().toString(), MyDashboardActivity.this);
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });


        TextView issueTV = (TextView) inflator.findViewById(R.id.issueTV_id);
        TextView header = (TextView) inflator.findViewById(R.id.textView);

        if (SingleTon.getInstance().getAddress() != null) {
            header.setText(SingleTon.getInstance().getAddress());
        } else {
            header.setText("Test Office");
        }
        alertDialog = builder.create();
        alertDialog.show();

    }


    private View.OnClickListener onOkButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }


        }
    };


    private View.OnClickListener onCancelButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    };


    @Override
    public void onBeaconSelected(Beacon beacon, String s, String s1) {
//        BeaconDialogFragment dialog = BeaconDialogFragment.newInstance(beacon);
//        dialog.show(getSupportFragmentManager(), DIALOG_BEACON);

        mayBeCalledPausedDailog(beacon);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

}
