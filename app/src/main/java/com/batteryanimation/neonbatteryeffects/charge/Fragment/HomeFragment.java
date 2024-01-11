package com.batteryanimation.neonbatteryeffects.charge.Fragment;


import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.AppOpenAds.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adsmodule.api.AdsModule.AdUtils;
import com.adsmodule.api.AdsModule.Interfaces.AppInterfaces;
import com.adsmodule.api.AdsModule.Utils.Constants;
import com.batteryanimation.neonbatteryeffects.charge.Activity.AnimationActivity;
import com.batteryanimation.neonbatteryeffects.charge.Activity.DashboardActivity;
import com.batteryanimation.neonbatteryeffects.charge.Activity.DownloadsActivity;
import com.batteryanimation.neonbatteryeffects.charge.Adapter.AnimationAdapter;
import com.batteryanimation.neonbatteryeffects.charge.Model.Wallpaper;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.Service.BatteryService;
import com.batteryanimation.neonbatteryeffects.charge.databinding.FragmentHomeBinding;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ShowDialogBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final int REQUEST_OVERLAY_PERMISSION = 123;
    FragmentHomeBinding binding;
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("level", -1);
            TextView textView = binding.tvBatteryPerCenter;
            textView.setText(((int) (((float) (intExtra * 100)) / ((float) intent.getIntExtra("scale", -1)))) + " ");
            binding.batteryLife.setText("Available : " + calculateBatteryDischargeTime(requireActivity()) + "h");
            binding.progressBar.setProgress((int) (((float) (intExtra * 100)) / ((float) intent.getIntExtra("scale", -1))));
        }
    };
    ArrayList<Wallpaper> newList;
    AnimationAdapter newAdapter;
//    TextView batteryBtm, homeBtm, animationBtm;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    Activity activity;

    //    @SuppressLint("ValidFragment")
//    public HomeFragment(DashboardActivity dashboardActivity) {
//        activity = dashboardActivity;
//    }
//    public static HomeFragment newInstance(String dashboardActivity) {
//        Bundle args = new Bundle();
//        args.putString("key", dashboardActivity);
//        HomeFragment homeFragment = new HomeFragment();
//        homeFragment.setArguments(args);
//        return homeFragment;
//    }


    public HomeFragment(DashboardActivity dashboardActivity) {
        activity = dashboardActivity;
    }

    public static boolean isMyServiceRunning(Class<?> cls, Context context) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public int getBatteryPercentage(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level == -1 || scale == -1) {
            return -1; // Error occurred
        }

        return (int) ((level / (float) scale) * 100);
    }

    public int calculateBatteryDischargeTime(Context context) {
        int batteryPercentage = getBatteryPercentage(context);

        if (batteryPercentage != -1) {
            // The battery consumption rate (in percentage per hour) is an estimate.
            // You may need to adjust this value based on device usage patterns.
            float consumptionRate = 5.0f; // 5% per hour (example value)

            int dischargeTime = (int) (batteryPercentage / consumptionRate);
            return dischargeTime;
        } else {
            return -1; // Unable to calculate discharge time due to an error
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

//        batteryBtm = activity.findViewById(R.id.battery_btm);
//        homeBtm = activity.findViewById(R.id.home_btm);
//        animationBtm = activity.findViewById(R.id.animation_btm);
        drawerLayout = activity.findViewById(R.id.drawLay);
        bottomNavigationView = activity.findViewById(R.id.bottom_nav);

        binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.newRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.newRecycler.setHasFixedSize(true);
        binding.newRecycler.setNestedScrollingEnabled(false);

        requireContext().registerReceiver(this.mBatInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));

        binding.nextbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {

                        bottomNavigationView.setSelectedItemId(R.id.battery_btm_nav);
//                        batteryBtm.setBackgroundResource(R.drawable.blur);
//                        batteryBtm.setTextColor(getResources().getColor(R.color.white));
//                        batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
//                        homeBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        homeBtm.setTextColor(getResources().getColor(R.color.grey));
//                        homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
//                        animationBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        animationBtm.setTextColor(getResources().getColor(R.color.grey));
//                        animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
//                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, BatteryFragment.newInstance("battery")).addToBackStack(null).commit();
//                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new BatteryFragment((DashboardActivity) activity)).addToBackStack(null).commit();
//                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new BatteryFragment()).addToBackStack(null).commit();

                    }
                });
            }
        });

        binding.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        /*animationBtm.setBackgroundResource(R.drawable.blur);
                        animationBtm.setTextColor(getResources().getColor(R.color.white));
                        animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                        homeBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                        homeBtm.setTextColor(getResources().getColor(R.color.grey));
                        homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                        batteryBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                        batteryBtm.setTextColor(getResources().getColor(R.color.grey));
                        batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new AnimationFragment((DashboardActivity) activity)).addToBackStack(null).commit();*/

                        startActivity(new Intent(requireActivity(), AnimationActivity.class).putExtra("load", "Animation"));

                    }
                });
            }
        });

        binding.downloadsbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, new AppInterfaces.InterstitialADInterface() {
                    @Override
                    public void adLoadState(boolean isLoaded) {
                        startActivity(new Intent(requireActivity(), DownloadsActivity.class));
                    }
                });

            }
        });

        newList = new ArrayList<>();
        newAdapter = new AnimationAdapter(getActivity(), (ArrayList<Wallpaper>) newList, 8);
        binding.newRecycler.setAdapter(newAdapter);
        try {
            InputStream inputStream = requireActivity().getAssets().open("category.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonConfig = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(jsonConfig);
            JSONObject categoriesObject = jsonObject.getJSONObject("Categories");

            processCategory(categoriesObject, "New", (ArrayList<Wallpaper>) newList);


            newAdapter.notifyDataSetChanged();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if (isMyServiceRunning(BatteryService.class, requireActivity())) {
        } else {
            Intent intent = new Intent(requireActivity(), BatteryService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                requireActivity().startForegroundService(intent);
            } else {
                requireActivity().startService(intent);
            }
        }


        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(this.mBatInfoReceiver);
    }

    private void processCategory(JSONObject categoriesObject, String categoryName, ArrayList<Wallpaper> categoryList) throws JSONException {
        if (categoriesObject.has(categoryName)) {
            JSONObject categoryObject = categoriesObject.getJSONObject(categoryName);
            JSONArray urlsArray = categoryObject.getJSONArray("urls");
            categoryList.clear();
            for (int i = 0; i < urlsArray.length(); i++) {
                String url = urlsArray.getString(i);
                Wallpaper wallpaper = new Wallpaper();
                wallpaper.setUrl(url);
                categoryList.add(wallpaper);
            }
        }
    }

    public void showDialog() {
        if (!Settings.canDrawOverlays(requireContext())) {
            // Show the dialog only if the overlay permission is not granted
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            ShowDialogBinding bind = ShowDialogBinding.inflate(LayoutInflater.from(requireActivity()));
            builder.setView(bind.getRoot());
            Dialog dialog = builder.create();
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            bind.btnOk.setOnClickListener(view -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireActivity().getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
                dialog.dismiss();
            });
        }
    }

}