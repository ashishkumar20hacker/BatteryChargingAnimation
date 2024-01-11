package com.batteryanimation.neonbatteryeffects.charge;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    Context context;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;

    public SharedPreferencesUtil(Context context2) {
        this.context = context2;
        SharedPreferences sharedPreferences = context2.getSharedPreferences(context2.getResources().getString(R.string.app_name), 0);
        this.sharedpreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public void isVideo(boolean z) {
        this.editor.putBoolean("isrVideo", z);
        this.editor.apply();
    }

    public boolean getIsVideo() {
        return this.sharedpreferences.getBoolean("isrVideo", false);
    }

    public void setVideoPath(String str) {
        this.editor.putString("videoPath", str);
        this.editor.apply();
    }

    public String getVideoPath() {
        return this.sharedpreferences.getString("videoPath", "");
    }

    public void setAnimationName(int i) {
        this.editor.putInt("animationName", i);
        this.editor.apply();
    }

    public void setClockStyle(int i) {
        this.editor.putInt("clockStyle", i);
        this.editor.apply();
    }

    public int getClockStyle() {
        return this.sharedpreferences.getInt("clockStyle", 1);
    }

    public void setAnimationSound(boolean z) {
        this.editor.putBoolean("animationSound", z);
        this.editor.apply();
    }

    public boolean getAnimationSound() {
        return this.sharedpreferences.getBoolean("animationSound", true);
    }

    public void setDateStyle(int i) {
        this.editor.putInt("dateFormat", i);
        this.editor.apply();
    }

    public int getDateStyle() {
        return this.sharedpreferences.getInt("dateFormat", 0);
    }

    public void setScreenTimeout(String str) {
        this.editor.putString("screenTimeOut", str);
        this.editor.apply();
    }

    public String getScreenTimeOut() {
        return this.sharedpreferences.getString("screenTimeOut", "10 Second");
    }
}
