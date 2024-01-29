package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.adsmodule.api.adsModule.models.AdsDataRequestModel;
import com.adsmodule.api.adsModule.retrofit.AdsApiHandler;
import com.adsmodule.api.adsModule.utils.AdUtils;
import com.adsmodule.api.adsModule.utils.AppInterfaces;
import com.adsmodule.api.adsModule.utils.Globals;
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

        if(Globals.isConnectingToInternet(SplashActivity.this)){
            AdsApiHandler.callAdsApi(activity, com.adsmodule.api.adsModule.utils.Constants.BASE_URL, new AdsDataRequestModel(getPackageName(), ""), adsResponseModel -> {
                if(adsResponseModel!=null){
                    AdUtils.showAppStartAd(activity, adsResponseModel, new AppInterfaces.AppStartInterface() {
                        @Override
                        public void loadStatus(boolean isLoaded) {
                            nextActivity();
                        }
                    });
                }
                else new Handler().postDelayed(this::nextActivity, 1000);
            });
        }
        else new Handler().postDelayed(this::nextActivity, 1500);


    }

    private void nextActivity() {
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {

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
//        }, 1200);
    }
}