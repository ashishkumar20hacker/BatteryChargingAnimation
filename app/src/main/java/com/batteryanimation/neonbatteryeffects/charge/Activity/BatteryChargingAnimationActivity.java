package com.batteryanimation.neonbatteryeffects.charge.Activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.InputDeviceCompat;
import androidx.work.WorkRequest;

import com.batteryanimation.neonbatteryeffects.charge.AnalogClock;
import com.batteryanimation.neonbatteryeffects.charge.OnItemClickListener.DoubleClickListener;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SharedPreferencesUtil;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;

public class BatteryChargingAnimationActivity extends AppCompatActivity {
    AnalogClock analogClockView;
    CountDownTimer countDownTimer;
    String[] dateFormats = {"EEEE, MMMM dd, yyyy", "EEE, MMMM dd, yyyy", "EEEE, dd MMMM", "dd MMMM yyyy"};
    LinearLayout digitalView;
    GifImageView ivPreview;
    RelativeLayout preview;
    PowerDis receiver;
    SharedPreferencesUtil sharedPreferencesUtil;
    TextView tvBatteryPerCenter;
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("level", -1);
            TextView textView = BatteryChargingAnimationActivity.this.tvBatteryPerCenter;
            textView.setText(((int) (((float) (intExtra * 100)) / ((float) intent.getIntExtra("scale", -1)))) + "%");
        }
    };
    TextView tvDate;
    TextView tvDate1;
    TextView tvTime;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        getWindow().getDecorView().setSystemUiVisibility(InputDeviceCompat.SOURCE_TOUCHSCREEN);
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(2621440);
        }
        setContentView((int) R.layout.activity_battery_charging_animation);

        this.sharedPreferencesUtil = new SharedPreferencesUtil(this);
        registerReceiver(this.mBatInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        this.ivPreview = (GifImageView) findViewById(R.id.ivPreview);
        this.preview = (RelativeLayout) findViewById(R.id.preview);
        this.tvBatteryPerCenter = (TextView) findViewById(R.id.tvBatteryPerCenter);
        this.tvTime = (TextView) findViewById(R.id.tvTime);
        this.tvDate = (TextView) findViewById(R.id.tvDate);
        this.tvDate1 = (TextView) findViewById(R.id.tvDate1);
        this.analogClockView = (AnalogClock) findViewById(R.id.analogClockView);
        this.digitalView = (LinearLayout) findViewById(R.id.digitalView);
        this.tvTime.setText(getTime());
        this.tvDate.setText(getDate());
        this.tvDate1.setText(getDate());
        if (this.sharedPreferencesUtil.getClockStyle() == 0) {
            this.analogClockView.setVisibility(0);
            this.digitalView.setVisibility(4);
            this.tvDate1.setVisibility(0);
            this.tvDate.setVisibility(4);
        } else {
            this.analogClockView.setVisibility(4);
            this.tvDate1.setVisibility(4);
            this.tvDate.setVisibility(0);
            this.digitalView.setVisibility(0);
        }
        this.tvBatteryPerCenter.setVisibility(0);
        this.sharedPreferencesUtil = new SharedPreferencesUtil(this);

        SharedPreferences otherPreferences = getSharedPreferences("MyOtherChargePreferences", Context.MODE_PRIVATE);
        String imageUrlChargeOther = otherPreferences.getString("imageUrlChargeOther", "");

        Glide.with(BatteryChargingAnimationActivity.this)
                .asGif()
                .load(imageUrlChargeOther)
                .into(ivPreview);
        this.preview.setOnClickListener(new DoubleClickListener() {
            public void onSingleClick(View view) {
            }

            public void onDoubleClick(View view) {
                BatteryChargingAnimationActivity.this.finishAndRemoveTask();
            }
        });
        String screenTimeOut = this.sharedPreferencesUtil.getScreenTimeOut();
        if (screenTimeOut.equals("No Timeout")) {
            return;
        }
        if (screenTimeOut.equals("10 Second")) {
            StartTimer(WorkRequest.MIN_BACKOFF_MILLIS);
        } else if (screenTimeOut.equals("30 Second")) {
            StartTimer(WorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS);
        } else {
            StartTimer(60000);
        }
    }


    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mBatInfoReceiver);
        unregisterReceiver(this.receiver);
        CountDownTimer countDownTimer2 = this.countDownTimer;
        if (countDownTimer2 != null) {
            countDownTimer2.cancel();
        }
    }

    public String getTime() {
        return new SimpleDateFormat("hh:mm").format(new Date());
    }

    public String getDate() {
        return new SimpleDateFormat(this.dateFormats[this.sharedPreferencesUtil.getDateStyle()]).format(new Date());
    }

    public void StartTimer(long j) {
        this.countDownTimer = new CountDownTimer(j, 1000) {
            public void onTick(long j) {
            }

            public void onFinish() {
//                BatteryChargingAnimationActivity.this.finishAndRemoveTask();
            }
        }.start();
    }


    public void onResume() {
        super.onResume();
        this.receiver = new PowerDis();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        registerReceiver(this.receiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        BatteryChargingAnimationActivity.super.onBackPressed();
    }

    public class PowerDis extends BroadcastReceiver {
        public PowerDis() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
                Log.e("Charger State", "power disconnected");
                BatteryChargingAnimationActivity.this.finishAndRemoveTask();
            }
        }
    }
}
