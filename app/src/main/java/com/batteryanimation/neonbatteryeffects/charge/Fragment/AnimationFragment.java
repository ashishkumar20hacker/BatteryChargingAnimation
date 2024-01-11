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
import com.batteryanimation.neonbatteryeffects.charge.Model.Wallpaper;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SetWallPaperListener;
import com.batteryanimation.neonbatteryeffects.charge.databinding.FragmentAnimationBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnimationFragment extends Fragment {

    FragmentAnimationBinding binding;
//    TextView batteryBtm, homeBtm, animationBtm;
//    Activity activity;

    List<Wallpaper> lockThemeModelArrayList;

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
//        lockThemeModelArrayList =readThemeJsonFromRaw(requireActivity());
        adapter = new WallpaperAdapter(requireActivity(), lockThemeModelArrayList, 8);
        binding.wallpaperRecycler.setAdapter(adapter);
        try {
            InputStream inputStream = requireActivity().getAssets().open("category.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonConfig = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(jsonConfig);
            JSONObject categoriesObject = jsonObject.getJSONObject("Categories");

            processCategory(categoriesObject, "Wallpaper", (ArrayList<Wallpaper>) lockThemeModelArrayList);

            adapter.notifyDataSetChanged();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return binding.getRoot();
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
}