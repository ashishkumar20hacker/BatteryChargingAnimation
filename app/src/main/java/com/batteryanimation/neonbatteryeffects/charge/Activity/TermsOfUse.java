package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.AppOpenAds.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adsmodule.api.AdsModule.AdUtils;
import com.adsmodule.api.AdsModule.Interfaces.AppInterfaces;
import com.adsmodule.api.AdsModule.Utils.Constants;
import com.batteryanimation.neonbatteryeffects.charge.R;

public class TermsOfUse extends AppCompatActivity {

    TextView agreebtn;
    String activityName;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = getResources().getDrawable(R.drawable.splashbg);
        getWindow().setBackgroundDrawable(background);
        setContentView(R.layout.activity_terms_of_use);
        agreebtn = findViewById(R.id.agreebtn);
//        AdUtils.showNativeAd(activity, Constants.adsResponseModel.getNative_ads().getAdx(), findViewById(R.id.native_ads).findViewById(com.adsmodule.api.R.id.native_ad_small), 2, null);
        activityName = getIntent().getStringExtra("activity");
        if (activityName.equals("db")) {
            agreebtn.setVisibility(View.GONE);
        } else {
            agreebtn.setVisibility(View.VISIBLE);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("string.activity");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("check", "broadcast received");
                finish();
            }
        };

        registerReceiver(receiver, intentFilter);

        agreebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        startActivity(new Intent(getApplicationContext(), PermissionsActivity.class));
                    }
                });
            }
        });
    }

    public void pp(View view) {
        gotoUrl("https://digitalcanvasstudios.blogspot.com/p/privacy-policy.html");
    }

    public void tmc(View view) {
        startActivity(new Intent(getApplicationContext(), TermsConditions.class));
//        gotoUrl("https://digifyningmediainc.blogspot.com/p/privacy-policy.html");
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public void onBackPressed() {
        AdUtils.showBackPressAds(activity, Constants.adsResponseModel.getApp_open_ads().getAdx(), new AppInterfaces.AppOpenADInterface() {
            @Override
            public void appOpenAdState(boolean state_load) {
                TermsOfUse.super.onBackPressed();
            }
        });
    }
}