package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.AppOpenAds.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.adsmodule.api.AdsModule.AdUtils;
import com.adsmodule.api.AdsModule.Interfaces.AppInterfaces;
import com.adsmodule.api.AdsModule.Utils.Constants;
import com.batteryanimation.neonbatteryeffects.charge.Adapter.AnimationAdapter;
import com.batteryanimation.neonbatteryeffects.charge.Adapter.WallpaperAdapter;
import com.batteryanimation.neonbatteryeffects.charge.Model.LockThemeModel;
import com.batteryanimation.neonbatteryeffects.charge.Model.Wallpaper;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SetWallPaperListener;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ActivityAnimationBinding;
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

public class AnimationActivity extends AppCompatActivity {

    ActivityAnimationBinding binding;
    ArrayList<Wallpaper> newList;
    AnimationAdapter newAdapter;
    List<LockThemeModel> lockThemeModelArrayList;
    WallpaperAdapter adapter;
    String load = "Animation";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = getResources().getDrawable(R.drawable.splashbg);
        getWindow().setBackgroundDrawable(background);
        binding = ActivityAnimationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.newRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        binding.newRecycler.setHasFixedSize(true);
        binding.newRecycler.setNestedScrollingEnabled(false);

        load = getIntent().getStringExtra("load");
        binding.title.setText(load);

        if (load.equals("Animation")) {
            setAnimation();
        } else {
            setWallpapers();
        }

    }

    @Override
    public void onBackPressed() {
        AdUtils.showBackPressAds(activity, Constants.adsResponseModel.getApp_open_ads().getAdx(), new AppInterfaces.AppOpenADInterface() {
            @Override
            public void appOpenAdState(boolean state_load) {
                /*AnimationActivity.super.onBackPressed();*/
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            }
        });
    }

    private void setWallpapers() {

        lockThemeModelArrayList = new ArrayList<>();
        lockThemeModelArrayList = readThemeJsonFromRaw(getApplicationContext());
        adapter = new WallpaperAdapter(getApplicationContext(), lockThemeModelArrayList, 0);
        binding.newRecycler.setAdapter(adapter);
    }

    private void setAnimation() {

        newList = new ArrayList<>();
        newAdapter = new AnimationAdapter(getApplicationContext(), (ArrayList<Wallpaper>) newList, 0);
        binding.newRecycler.setAdapter(newAdapter);
        try {
            InputStream inputStream = getAssets().open("category.json");
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