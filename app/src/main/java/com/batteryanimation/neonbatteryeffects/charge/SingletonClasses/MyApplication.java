package com.batteryanimation.neonbatteryeffects.charge.SingletonClasses;

import static com.adsmodule.api.AdsModule.Retrofit.APICallHandler.callAppCountApi;
import static com.adsmodule.api.AdsModule.Utils.Constants.MAIN_BASE_URL;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.adsmodule.api.AdsModule.PreferencesManager.AppPreferences;
import com.adsmodule.api.AdsModule.Retrofit.AdsDataRequestModel;
import com.adsmodule.api.AdsModule.Utils.ConnectionDetector;
import com.adsmodule.api.AdsModule.Utils.Global;
import com.batteryanimation.neonbatteryeffects.charge.R;

public class MyApplication extends Application {

    private static MyApplication app;
    private static ConnectionDetector cd;
    static AppPreferences preferences;

    public static AppPreferences getPreferences() {
        if (preferences == null)
            preferences = new AppPreferences(app);
        return preferences;
    }
    public static Context context1;
//    public static BatteryLowReceiver batteryLowReceiver;


    public static ConnectionDetector getConnectionStatus(){
        if (cd == null){
            cd=new ConnectionDetector(app);
        }
        return cd;
    }



    public static synchronized MyApplication getInstance() {
        return app;
    }





    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context1 = getApplicationContext();
        AppPreferences preferences = new AppPreferences(app);
        if (preferences.isFirstRun()) {
            callAppCountApi(MAIN_BASE_URL, new AdsDataRequestModel(app.getPackageName(), Global.getDeviceId(app)), () -> {
                preferences.setFirstRun(false);
            });
        }

        new AppOpenAds(app);
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