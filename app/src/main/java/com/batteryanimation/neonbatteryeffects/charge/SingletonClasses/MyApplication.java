package com.batteryanimation.neonbatteryeffects.charge.SingletonClasses;


import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.multidex.MultiDex;

import com.adsmodule.api.adsModule.enums.Panel;
import com.adsmodule.api.adsModule.models.AdsDataRequestModel;
import com.adsmodule.api.adsModule.retrofit.AdsApiHandler;
import com.adsmodule.api.adsModule.utils.AppPreferences;
import com.adsmodule.api.adsModule.utils.AudienceNetworkInitializeHelper;
import com.adsmodule.api.adsModule.utils.ConnectionDetector;
import com.adsmodule.api.adsModule.utils.Constants;
import com.adsmodule.api.adsModule.utils.Globals;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;

public class MyApplication extends Application {

    private static MyApplication app;
    private static final String TAG = "MyApplication";

    public static AppPreferences preferences;
    public static AppPreferences getPreference(){
        if(preferences == null){
            preferences = new AppPreferences(app.getApplicationContext(), R.string.app_name);
        }
        return preferences;
    }

    private void setUpConnectionDetector(){
        ConnectionDetector detector= new ConnectionDetector(app);
        detector.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAvailable) {
                Constants.IS_NETWORK_AVAILABLE= isAvailable;
                if(isAvailable){
                    if(Constants.adsResponseModel.getPackage_name() == null /*|| Constants.adsResponseModel.getPackage_name().isEmpty()*/){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(activity != null && !activity.isFinishing() && !activity.getComponentName().getClassName().equals("com.google.android.gms.ads.AdActivity")){
                                    AdsApiHandler.callAdsApi(activity, Constants.BASE_URL, new AdsDataRequestModel(getPackageName(), ""), adsResponseModel -> {});
                                }
                            }
                        }, 3000);
                    }
                    Log.e(TAG, "onChanged: Network Available");
                }
                else Log.e(TAG, "onChanged: Network not Available");
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Static Application Context
        app = this;
        Globals.mobileAdsInitializer(app.getApplicationContext());

        AudienceNetworkInitializeHelper.initialize(app.getApplicationContext());
        //Preference Initialization
        preferences = getPreference();
        setUpConnectionDetector();
        // UnComment this line if you require onesignal
        // Globals.initOneSignal(app.getApplicationContext(), "**YOUR_ONE_SIGNAL_APP_ID**");

        // Increase app download count
        if (preferences.isAdsFirstRun()) {
            // {Panel.IDE | Panel.D2M} -> Select from any one of these Panel according to your implemented Panel
            AdsApiHandler.callAppCountApi(Panel.D2M, new AdsDataRequestModel(getPackageName(), Globals.getDeviceId(this)), baseUrl -> {
                preferences.setAdsFirstRun(false);
                preferences.setAdsBaseUrl(baseUrl);
            });
        } else Constants.BASE_URL= preferences.getAdsBaseUrl();


        new FlurryAgent.Builder()
                .withDataSaleOptOut(false)
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.DEBUG)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, "VR56664NPMWFPTJWF7SQ");
        //LifeCycle Owner Initialization
        new LifeCycleOwner(app);
        createNotificationChannel();
//        registerReceiver(batteryLowReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    public static final String CHANNEL_ID = "BATTERY_CHARGING_ANIMATION";
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH));
        }
    }
    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
