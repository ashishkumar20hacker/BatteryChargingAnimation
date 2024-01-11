package com.batteryanimation.neonbatteryeffects.charge;

import android.content.Context;
import android.content.SharedPreferences;

//import com.google.gson.Gson;

public class SharePreferences {

    private Context applicationContext;
//    private Gson gson;
    private SharedPreferences sharedPreferences;

    public SharePreferences(Context applicationContext) {
        this.applicationContext = applicationContext;
//        gson = new Gson();
        String preferencesName = applicationContext.getString(R.string.app_name);
        sharedPreferences = applicationContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    public void putString(String key,String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putInt(String key,int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void putBoolean(String key,boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

}
