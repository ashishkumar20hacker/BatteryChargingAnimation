package com.batteryanimation.neonbatteryeffects.charge;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.adsmodule.api.adsModule.models.AdsResponseModel;
import com.batteryanimation.neonbatteryeffects.charge.Model.Wallpaper;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static ReviewManager reviewManager;

    public static void showRateApp(Activity contexts) {
        reviewManager = ReviewManagerFactory.create(contexts);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Getting the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(contexts, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown.
                });

                flow.addOnFailureListener(e -> {
                    rateApp(contexts);
                });
            }
        });
    }

    public static void rateApp(Activity contexts) {
        try {
            Intent rateIntent = rateIntentForUrl(contexts,"market://details");
            contexts.startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl(contexts, "https://play.google.com/store/apps/details");
            contexts.startActivity(rateIntent);
        }
    }

    private static Intent rateIntentForUrl(Activity contexts, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, contexts.getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public static void makeStatusBarTransparent(Activity context) {
        if (SDK_INT >= 19 && SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        //make fully Android Transparent Status bar
        if (SDK_INT >= 21) {
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void makeStatusBarTransparent2(Activity context) {
        if (SDK_INT >= 19 && SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (SDK_INT >= 21) {
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        /*WindowInsetsControllerCompat.setAppearanceLightStatusBars(true)*/
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static ArrayList<Wallpaper> getWallpapers() {
        ArrayList<Wallpaper> list = new ArrayList<>();

        if (com.adsmodule.api.adsModule.utils.Constants.adsResponseModel != null && com.adsmodule.api.adsModule.utils.Constants.adsResponseModel.getExtra_data_field() != null) {
            AdsResponseModel.ExtraDataFieldDTO extraDataFieldDTO = com.adsmodule.api.adsModule.utils.Constants.adsResponseModel.getExtra_data_field();


            if (extraDataFieldDTO.getData() != null) {
                List<AdsResponseModel.ExtraDataFieldDTO.Datum> categories = extraDataFieldDTO.getData();

                for (AdsResponseModel.ExtraDataFieldDTO.Datum myCategory : categories) {
                    Log.d("Festival Name", myCategory.getDataType());
                    Log.d("Image", myCategory.getUrls().get(0));

                    if (myCategory.getUrls() != null && myCategory.getDataType().equals("Wallpaper")) {
                        for (String url : myCategory.getUrls()){
                            list.add(new Wallpaper(url));
                        }
                    } else {
                        Log.d("Error", "MyCategory data is null");
                    }
                }
            } else {
                Log.d("Error", "Categories is null");
            }
        } else {
            Log.d("Error", "Constants.adsResponseModel or ExtraDataFieldDTO is null");
        }

        return list;
    }
    public static ArrayList<Wallpaper> getChargingAnimations() {
        ArrayList<Wallpaper> list = new ArrayList<>();

        if (com.adsmodule.api.adsModule.utils.Constants.adsResponseModel != null && com.adsmodule.api.adsModule.utils.Constants.adsResponseModel.getExtra_data_field() != null) {
            AdsResponseModel.ExtraDataFieldDTO extraDataFieldDTO = com.adsmodule.api.adsModule.utils.Constants.adsResponseModel.getExtra_data_field();


            if (extraDataFieldDTO.getData() != null) {
                List<AdsResponseModel.ExtraDataFieldDTO.Datum> categories = extraDataFieldDTO.getData();

                for (AdsResponseModel.ExtraDataFieldDTO.Datum myCategory : categories) {
                    Log.d("Festival Name", myCategory.getDataType());
                    Log.d("Image", myCategory.getUrls().get(0));

                    if (myCategory.getUrls() != null && myCategory.getDataType().equals("Charging")) {
//                        list = myCategory.getUrls();
                        for (String url : myCategory.getUrls()){
                            list.add(new Wallpaper(url));
                        }
                    } else {
                        Log.d("Error", "MyCategory data is null");
                    }
                }
            } else {
                Log.d("Error", "Categories is null");
            }
        } else {
            Log.d("Error", "Constants.adsResponseModel or ExtraDataFieldDTO is null");
        }

        return list;
    }
}
