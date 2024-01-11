package com.batteryanimation.neonbatteryeffects.charge.Fragment;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.AppOpenAds.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adsmodule.api.AdsModule.AdUtils;
import com.adsmodule.api.AdsModule.Interfaces.AppInterfaces;
import com.adsmodule.api.AdsModule.Utils.Constants;
import com.batteryanimation.neonbatteryeffects.charge.Activity.AnimationActivity;
import com.batteryanimation.neonbatteryeffects.charge.Activity.AnimationPreviewActivity;
import com.batteryanimation.neonbatteryeffects.charge.Activity.DashboardActivity;
import com.batteryanimation.neonbatteryeffects.charge.Adapter.WallpaperAdapter;
import com.batteryanimation.neonbatteryeffects.charge.Model.LockThemeModel;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SetWallPaperListener;
import com.batteryanimation.neonbatteryeffects.charge.databinding.FragmentAnimationBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnimationFragment extends Fragment {

    FragmentAnimationBinding binding;
//    TextView batteryBtm, homeBtm, animationBtm;
//    Activity activity;

    List<LockThemeModel> lockThemeModelArrayList;

    WallpaperAdapter adapter;
   /* public AnimationFragment(DashboardActivity dashboardActivity) {
        activity = dashboardActivity;
    }*/

//    public static AnimationFragment newInstance(String dashboardActivity) {
//        Bundle args = new Bundle();
//        args.putString("key", dashboardActivity);
//        AnimationFragment animationFragment = new AnimationFragment();
//        animationFragment.setArguments(args);
//        return animationFragment;
//    }


    public AnimationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimationBinding.inflate(inflater, container, false);

//        batteryBtm = activity.findViewById(R.id.battery_btm);
//        homeBtm = activity.findViewById(R.id.home_btm);
//        animationBtm = activity.findViewById(R.id.animation_btm);

        LinearGradient shader = new
                LinearGradient(0f, 0f, 0f, binding.title2.getTextSize(), Color.parseColor("#63D0D0"), Color.parseColor("#0A99A4"), Shader.TileMode.CLAMP);
        binding.title2.getPaint().setShader(shader);

        binding.wallpaperRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.wallpaperRecycler.setHasFixedSize(true);
        binding.wallpaperRecycler.setNestedScrollingEnabled(false);

        binding.backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showBackPressAds(activity, Constants.adsResponseModel.getInterstitial_ads().getAdx(), new AppInterfaces.AppOpenADInterface() {
                    @Override
                    public void appOpenAdState(boolean state_load) {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            }
        });

        binding.chargingAnimationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, isLoaded -> {
                    startActivity(new Intent(requireActivity(), AnimationActivity.class).putExtra("load", "Animation"));
                });
            }
        });

       binding.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, isLoaded -> {
                    startActivity(new Intent(requireActivity(), AnimationActivity.class).putExtra("load", "Wallpaper"));
                });
            }
        });

        lockThemeModelArrayList = new ArrayList<>();
        lockThemeModelArrayList =readThemeJsonFromRaw(requireActivity());
        adapter = new WallpaperAdapter(requireActivity(), lockThemeModelArrayList, 8);
        binding.wallpaperRecycler.setAdapter(adapter);

        return binding.getRoot();
    }
/*
    public static List<LockThemeModel> readThemeJsonFromRaw(Context context) {
        List<LockThemeModel> lockThemeModels = new ArrayList<>();

        try {
            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.themes);
            Scanner scanner = new Scanner(inputStream);

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                LockThemeModel model = new Gson().fromJson(object.toString(), LockThemeModel.class);
                lockThemeModels.add(model);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        lockThemeModels.sort((t1, t2) -> t1.getTitle().toLowerCase().compareToIgnoreCase(t2.getTitle().toLowerCase()));

        return lockThemeModels;
    }*/

    public static List<LockThemeModel> readThemeJsonFromRaw(Context context) {
        List<LockThemeModel> lockThemeModels = new ArrayList<>();
        List<LockThemeModel> lockThemeModels2 = new ArrayList<>();

        try {
            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.themes);
            Scanner scanner = new Scanner(inputStream);

            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                LockThemeModel model = new Gson().fromJson(object.toString(), LockThemeModel.class);
                for (LockThemeModel themeModel : model.getThemes()) {
                    lockThemeModels.add(themeModel);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        lockThemeModels.sort((t1, t2) -> t1.getTitle().toLowerCase().compareToIgnoreCase(t2.getTitle().toLowerCase()));

        return lockThemeModels;
    }

}