package com.RareMediaCompany.BDPro.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.RareMediaCompany.BDPro.R;

/**
 * Created by sidd on 11/23/16.
 */

public class BaseActivity extends AppCompatActivity {

    public static Typeface mFont;
    public static Typeface mFontRegular;
    public static Typeface mFontBold;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFont = Typeface.createFromAsset(getAssets(), "fonts/source-sans-pro.light.ttf");
        mFontRegular = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        mFontBold = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Bold.ttf");
    }


    public void closeKeyBoard() {

            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
    }

    public void openKeyBoard(View view) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            view.requestFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showSnackbar(View view, String message, String actionText, View.OnClickListener onClickListener, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        if (actionText != null && onClickListener != null) {
            snackbar.setAction(actionText, onClickListener);
            snackbar.setActionTextColor(getColor(R.color.colorPrimaryDark));
        }
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getColor(R.color.colorWhite));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getColor(R.color.colorAccent));
        snackbar.show();
    }

    public void showSnackbar(View view, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showSnackbar(view, message, null, null, Snackbar.LENGTH_LONG);
        }
    }

    public void showSnackbar(View view, String message, int duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showSnackbar(view, message, null, null, duration);
        }
    }

    public void showSnackbar(View view, @StringRes int resId, @StringRes int actionResId, View.OnClickListener onClickListener) {
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showSnackbar(view, view.getResources().getString(resId), view.getResources().getString(actionResId), onClickListener, Snackbar.LENGTH_LONG);
            }
        }
    }

    public void showSnackbar(View view, @StringRes int resId) {
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showSnackbar(view, view.getResources().getString(resId), null, null, Snackbar.LENGTH_LONG);
            }
        }
    }

}
