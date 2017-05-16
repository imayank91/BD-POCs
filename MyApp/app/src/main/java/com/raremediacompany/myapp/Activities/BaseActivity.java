package com.raremediacompany.myapp.Activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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


}
