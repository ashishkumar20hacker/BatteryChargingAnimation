package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static android.os.Build.VERSION.SDK_INT;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.batteryanimation.neonbatteryeffects.charge.R;

public class PermissionsActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    ImageView pChecker;
    TextView agreebtn;
    String activityName;
    CardView adsView0, adsView;

    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = getResources().getDrawable(R.drawable.splashbg);
        getWindow().setBackgroundDrawable(background);
        setContentView(R.layout.activity_permissions);
        pChecker = findViewById(R.id.p_checker);
        agreebtn = findViewById(R.id.agreebtn);
        adsView = findViewById(R.id.adsView);
        adsView0 = findViewById(R.id.adsView0);

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

        activityName = getIntent().getStringExtra("activityName");
//        AdUtils.showNativeAd(PermissionsActivity.this, Constants.adsResponseModel.getNative_ads().getAdx(), adsView, 1, getDrawable(R.drawable.img_main_ads));
//        AdUtils.showNativeAd(PermissionsActivity.this, Constants.adsResponseModel.getNative_ads().getAdx(), adsView0, 2, null);


        pChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckPermission();
            }
        });

        agreebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (agreebtn.getText().equals("Next")) {

                    AdUtils.showInterstitialAd(activity, isLoaded -> {
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            Intent local = new Intent();
                            local.setAction("string.activity");
                            sendBroadcast(local);

                    });

                } else {
                    mCheckPermission();
                }
            }
        });

    }

    private void mCheckPermission() {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED/* || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED*/) {
//                Toast.makeText(PermissionsActivity.this, "Please provide permissions!!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, STORAGE_PERMISSION_CODE);

            } else {
                // Permission already granted, you can proceed with reading and writing external storage
                System.out.println("Permission Already Granted");
                pChecker.setImageResource(R.drawable.checked);
                agreebtn.setText("Next");
//                startActivity(new Intent(getApplicationContext(), TMCUseActivity.class));
            }
        } else {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED/* || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED*/) {
//                Toast.makeText(PermissionsActivity.this, "Please provide permissions!!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                // Permission already granted, you can proceed with reading and writing external storage
                System.out.println("Permission Already Granted");
                pChecker.setImageResource(R.drawable.checked);
                agreebtn.setText("Next");
//                startActivity(new Intent(getApplicationContext(), TMCUseActivity.class));
            }
        }
    }

    @Override
    public void onBackPressed() {
        AdUtils.showBackPressAd(activity, isLoaded -> {
                PermissionsActivity.super.onBackPressed();
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PermissionsActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                System.out.println("Permission Already Granted");
                pChecker.setImageResource(R.drawable.checked);
                agreebtn.setText("Next");
//                startActivity(new Intent(getApplicationContext(), TMCUseActivity.class));
            } else {
                Toast.makeText(PermissionsActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                mCheckPermission();
            }
        }
    }

}