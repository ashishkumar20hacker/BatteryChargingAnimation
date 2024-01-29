package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.batteryanimation.neonbatteryeffects.charge.Adapter.OnBoardingAdapter;
import com.batteryanimation.neonbatteryeffects.charge.Model.DataModel;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ActivityOnboardingBinding;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    ActivityOnboardingBinding binding;
    OnBoardingAdapter adapter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding);

        adapter = new OnBoardingAdapter();
        adapter.submitList(getBoardList());
        binding.viewPager.setAdapter(adapter);
//        AdUtils.showNativeAd(this, Constants.adsResponseModel.getNative_ads().getAdx(), binding.adsView0, 2, null);

        setOnBoardingIndicator();
        setCurrentOnBoardingIndicators(0);

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicators(position);
            }
        });

        binding.btnNext.setOnClickListener(view -> {
            if (binding.viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
            } else {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                    startActivity(new Intent(getApplicationContext(), TermsOfUse.class).putExtra("activity", "ob"));
                });
            }
        });
    }

    private void setOnBoardingIndicator() {
        ImageView[] indicators = new ImageView[3];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unfilled));
            indicators[i].setLayoutParams(layoutParams);
            binding.dotsLayout.addView(indicators[i]);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentOnBoardingIndicators(int index) {
        int childCount = binding.dotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.dotsLayout.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_filled));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unfilled));
            }
        }
    }

    private List<DataModel> getBoardList() {
        List<DataModel> list = new ArrayList<>();


        list.add(new DataModel(getString(R.string.title_1), getString(R.string.desc_1), R.drawable.img_onbaording1, 1));
        list.add(new DataModel(getString(R.string.title_2), getString(R.string.desc_2), R.drawable.img_onbaording2, 2));
        list.add(new DataModel(getString(R.string.title_3), getString(R.string.desc_3), R.drawable.img_onbaording3, R.drawable.onboarding_two));

        return list;
    }
}