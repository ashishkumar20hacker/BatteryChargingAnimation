package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.batteryanimation.neonbatteryeffects.charge.Adapter.FavouritesLiveAdapter;
import com.batteryanimation.neonbatteryeffects.charge.Model.Wallpaper;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ActivityDownloadsBinding;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ActivityFavouritesBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavouritesActivity extends AppCompatActivity {

    private FavouritesLiveAdapter favouritesAdapter;
    private ArrayList<Wallpaper> favoriteDataList;
    ActivityFavouritesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = getResources().getDrawable(R.drawable.splashbg);
        getWindow().setBackgroundDrawable(background);
        binding = ActivityFavouritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        favoriteDataList = loadFavoriteDataList();
        if (favoriteDataList.isEmpty()){
            binding.empty.setVisibility(View.VISIBLE);
        } else {
            binding.empty.setVisibility(View.GONE);
            binding.favRv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
            favouritesAdapter = new FavouritesLiveAdapter(FavouritesActivity.this, favoriteDataList);
            binding.favRv.setAdapter(favouritesAdapter);
        }

    }

    @Override
    public void onBackPressed() {
        AdUtils.showBackPressAd(activity, isLoaded -> {
                FavouritesActivity.super.onBackPressed();
        });

    }

    private ArrayList<Wallpaper> loadFavoriteDataList() {
        ArrayList<Wallpaper> favoriteList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(AnimationPreviewActivity.FAVORITES_PREF_NAME_LIVE_CHARGE, Context.MODE_PRIVATE);
        Set<String> favoritesSet = sharedPreferences.getStringSet(AnimationPreviewActivity.FAVORITES_PREF_NAME_LIVE_CHARGE, new HashSet<>());
        for (String imageUrl : favoritesSet) {
            Wallpaper wallpaper = new Wallpaper(imageUrl);
            favoriteList.add(wallpaper);
        }

        return favoriteList;
    }
}