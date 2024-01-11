package com.batteryanimation.neonbatteryeffects.charge.OnItemClickListener;

import android.view.View;

public abstract class DoubleClickListener implements View.OnClickListener {
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    long lastClickTime = 0;

    public abstract void onDoubleClick(View view);

    public abstract void onSingleClick(View view);

    public void onClick(View view) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(view);
        } else {
            onSingleClick(view);
        }
        this.lastClickTime = currentTimeMillis;
    }
}
