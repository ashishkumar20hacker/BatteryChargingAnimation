package com.batteryanimation.neonbatteryeffects.charge.Fragment;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.batteryanimation.neonbatteryeffects.charge.Activity.DashboardActivity;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.databinding.FragmentBatteryBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class BatteryFragment extends Fragment {

    FragmentBatteryBinding binding;
    Activity activity;

    //    @SuppressLint("ValidFragment")
//    public BatteryFragment(DashboardActivity dashboardActivity) {
//        activity = dashboardActivity;
//    }
//    public static BatteryFragment newInstance(String dashboardActivity) {
//        Bundle args = new Bundle();
//        args.putString("key", dashboardActivity);
//        BatteryFragment batteryFragment = new BatteryFragment();
//        batteryFragment.setArguments(args);
//        return batteryFragment;
//    }


    public BatteryFragment(DashboardActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBatteryBinding.inflate(inflater, container, false);


        TabLayout.Tab firstTab = binding.tabLayout.newTab();
        firstTab.setText("Information");
        binding.tabLayout.addTab(firstTab, true);
        TabLayout.Tab secondTab = binding.tabLayout.newTab();
        secondTab.setText("History");
        binding.tabLayout.addTab(secondTab);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_lay, new InformationFragment(activity, secondTab)).commit();

        binding.backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showBackPressAd(activity, isLoaded -> {
                        requireActivity().getSupportFragmentManager().popBackStack();
                });
            }
        });

        binding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (firstTab.isSelected()) {
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_lay, new InformationFragment(activity, secondTab)).commit();
                } else {
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_lay, new HistoryFragment()).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return binding.getRoot();
    }
}