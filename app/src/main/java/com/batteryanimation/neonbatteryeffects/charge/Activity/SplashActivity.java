package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.adsmodule.api.AdsModule.Utils.Global.checkAppVersion;
import static com.adsmodule.api.AdsModule.Utils.StringUtils.isNull;
import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.AppOpenAds.activity;
import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.MyApplication.getConnectionStatus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.adsmodule.api.AdsModule.AdUtils;
import com.adsmodule.api.AdsModule.Retrofit.APICallHandler;
import com.adsmodule.api.AdsModule.Retrofit.AdsDataRequestModel;
import com.adsmodule.api.AdsModule.Utils.Constants;
import com.adsmodule.api.AdsModule.Utils.Global;
import com.batteryanimation.neonbatteryeffects.charge.R;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_splash);

        if (getConnectionStatus().isConnectingToInternet()) {
            APICallHandler.callAdsApi(Constants.MAIN_BASE_URL, new AdsDataRequestModel(this.getPackageName(), ""), adsResponseModel -> {
                if (adsResponseModel != null) {
                    Constants.adsResponseModel = adsResponseModel;
                    Constants.hitCounter = adsResponseModel.getAds_count();
                    Constants.BACKPRESS_COUNT = adsResponseModel.getBackpress_count();
                    if (!isNull(Constants.adsResponseModel.getMonetize_platform()))
                        Constants.platformList = Arrays.asList(Constants.adsResponseModel.getMonetize_platform().split(","));
                    if (checkAppVersion(adsResponseModel.getVersion_name(), activity)) {
                        Global.showUpdateAppDialog(activity);
                    } else {
                        AdUtils.buildAppOpenAdCache(activity, Constants.adsResponseModel.getApp_open_ads().getAdx());
                        AdUtils.buildNativeCache(Constants.adsResponseModel.getNative_ads().getAdx(), activity);
                        AdUtils.buildInterstitialAdCache(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity);
                        AdUtils.buildRewardAdCache(Constants.adsResponseModel.getRewarded_ads().getAdx(), activity);
                        AdUtils.showAppOpenAds(Constants.adsResponseModel.getApp_open_ads().getAdx(), activity, state_load -> {
                            nextActivity();
                        });


                    }
                }

            });
        } else {
            nextActivity();
        }

    }

    private void nextActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            Boolean isFirstRun = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isFirstRun", true);
            if (isFirstRun) {
//
                Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish();

            } else {

                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
//
        }, 1200);
    }
}