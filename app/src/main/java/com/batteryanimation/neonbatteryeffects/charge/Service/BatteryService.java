package com.batteryanimation.neonbatteryeffects.charge.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.batteryanimation.neonbatteryeffects.charge.Activity.BatteryChargingAnimationActivity;
import com.batteryanimation.neonbatteryeffects.charge.Activity.DashboardActivity;
import com.batteryanimation.neonbatteryeffects.charge.Constants;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SharePreferences;
import com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.MyApplication;


public class BatteryService extends Service {
    public final IBinder iBinder = new LocalBinder();
    PowerConnectionReceiver receiver;
    BatteryLowReceiver receiver2;

    public int onStartCommand(Intent intent, int i, int i2) {
        return Service.START_STICKY;
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BatteryService getService() {
            return BatteryService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return this.iBinder;
    }

    public void onCreate() {
        super.onCreate();
        try {
            showNotification(getApplicationContext());
            this.receiver = new PowerConnectionReceiver();
            this.receiver2 = new BatteryLowReceiver();
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction(Intent.ACTION_BATTERY_CHANGED);
            intentFilter2.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter2.addAction(Intent.ACTION_POWER_DISCONNECTED);
            IntentFilter intentFilter = new IntentFilter();

            intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
            intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
            registerReceiver(this.receiver, intentFilter);
            registerReceiver(this.receiver2, intentFilter2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        try {
            unregisterReceiver(this.receiver);
            unregisterReceiver(this.receiver2);
        } catch (Exception unused) {
        }
        super.onDestroy();
    }

    public Notification showNotification(Context context) {
        int i = 33554432;
        PendingIntent activity2;
        PendingIntent activity = PendingIntent.getActivity(this, 0, new Intent(this, DashboardActivity.class), Build.VERSION.SDK_INT >= 31 ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_ONE_SHOT);
        Intent intent = new Intent(this, BatteryChargingAnimationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (Build.VERSION.SDK_INT < 31) {
//            i = 1073741824;
            activity2 = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            activity2 = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(2, new NotificationCompat.Builder((Context) this, MyApplication.CHANNEL_ID)
                    .setContentTitle("Battery Charging Animation")
                    .setPriority(1)
                    .setContentText("Service Started")
                    .setSmallIcon((int) R.drawable.battery)
                    .setFullScreenIntent(activity2, true).setContentIntent(activity).setCategory(NotificationCompat.CATEGORY_ALARM).build());
        } else {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(2, new NotificationCompat.Builder(this)
                            .setContentTitle("Battery Charging Animation")
                            .setPriority(1).setContentText("Service Started")
                            .setSmallIcon((int) R.drawable.battery)
                            .setContentIntent(activity).setCategory(NotificationCompat.CATEGORY_ALARM).setFullScreenIntent(activity2, true).build());
        }


        if (Build.VERSION.SDK_INT >= 26) {
            return new NotificationCompat.Builder((Context) this, MyApplication.CHANNEL_ID)
                    .setContentTitle("Battery Charging Animation")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText("Service Started")
                    .setSmallIcon((int) R.drawable.battery)
                    .setFullScreenIntent(activity2, true)
                    .setContentIntent(activity)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .build();
        } else {
            return new NotificationCompat.Builder(this)
                    .setContentTitle("Battery Charging Animation")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentText("Service Started")
                    .setSmallIcon((int) R.drawable.battery)
                    .setContentIntent(activity)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(activity2, true)
                    .build();
        }
    }

    public class PowerConnectionReceiver extends BroadcastReceiver {
        public PowerConnectionReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.d("tagCheck", "onReceive: " +intent.getAction());
            if (intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED")) {
                Log.d( "tagCheck","Check ");
                SharePreferences preferences = new SharePreferences(context);
                if (preferences.getBoolean(Constants.isAnimationSet)) {
                    Intent intent2 = new Intent(context, BatteryChargingAnimationActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                return;
            }
            intent.getAction().equals("android.intent.action.ACTION_POWER_DISCONNECTED");
        }
    }

    public class BatteryLowReceiver extends BroadcastReceiver {

        private static final int NOTIFICATION_ID = 1;
        private static final String CHANNEL_ID = "battery_low_channel";
        private static final String CHANNEL_NAME = "Battery Low Channel";
        private SharePreferences sharePreferences;

        @Override
        public void onReceive(Context context, Intent intent) {
            sharePreferences = new SharePreferences(context.getApplicationContext());
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryPct = (level / (float) scale) * 100;

                // Define the battery threshold for low battery (adjust as needed)
                float lowBatteryThreshold = sharePreferences.getInt(Constants.BATTERY_LOW); // Example threshold: 20%
                float fullBatteryThreshold = sharePreferences.getInt(Constants.CHARGING_COMPLETE); // Example threshold: 80%
                int status = intent.getIntExtra("status", 0);
                if (batteryPct <= lowBatteryThreshold) {
                    // Battery is low, create and show a notification
                    if (sharePreferences.getBoolean(Constants.isSwitch2On) && ((status == BatteryManager.BATTERY_STATUS_DISCHARGING) || (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING))) {
                        showLowBatteryNotification(context, intent);
                    }
                } else if ((batteryPct >= fullBatteryThreshold)) {
                    if (sharePreferences.getBoolean(Constants.isSwitch1On) && (status == BatteryManager.BATTERY_STATUS_CHARGING)) {
                        showFullBatteryNotification(context, intent);
                    }
                }
            }
        }

        private void showLowBatteryNotification(Context context, Intent intent) {
            // Create a notification channel (required for Android Oreo and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.battery)
                    .setContentTitle("Low Battery Warning")
                    .setContentText("Your device battery is low. Please charge it.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL))
                    .setAutoCancel(true);

            // Show the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());

            if (intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED")){
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }

        private void showFullBatteryNotification(Context context, Intent intent) {
            // Create a notification channel (required for Android Oreo and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.battery)
                    .setContentTitle("Battery Charged")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL))
                    .setAutoCancel(true);

            // Show the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            if (intent.getAction().equals("android.intent.action.ACTION_POWER_DISCONNECTED")){
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }
    }
}
