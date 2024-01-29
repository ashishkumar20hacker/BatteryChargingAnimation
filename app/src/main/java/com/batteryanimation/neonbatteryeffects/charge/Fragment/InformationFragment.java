package com.batteryanimation.neonbatteryeffects.charge.Fragment;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.batteryanimation.neonbatteryeffects.charge.Constants;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SharePreferences;
import com.batteryanimation.neonbatteryeffects.charge.databinding.FragmentInformationBinding;
import com.google.android.material.tabs.TabLayout;


public class InformationFragment extends Fragment {

    FragmentInformationBinding binding;

    IntentFilter intentfilter;
    float batteryTemp, fullVoltage;
    int batteryVol, deviceHealth;
    boolean isSwitch1On = false, isSwitch2On = false;
    Activity activity;
    TabLayout.Tab secondTab;
    private BatteryManager batteryManager;
    private final BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("level", -1);
            TextView textView = binding.tvBatteryPerCenter;
            textView.setText(((int) (((float) (intExtra * 100)) / ((float) intent.getIntExtra("scale", -1)))) + " ");
            binding.batteryLife.setText("Available : " + calculateBatteryDischargeTime(requireActivity()) + "h");
            binding.progressBar.setProgress((int) (((float) (intExtra * 100)) / ((float) intent.getIntExtra("scale", -1))));

            if (requireActivity() != null) {
                batteryManager = (BatteryManager) requireActivity().getSystemService(Context.BATTERY_SERVICE);
            }

            long cu = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            binding.actualTv.setText(String.valueOf(cu));
            //temprature
            batteryTemp = (float) (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
            binding.temperatureTv.setText(batteryTemp + " " + (char) 0x00B0 + "C");

            //voltage
            batteryVol = (int) (intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0));
            fullVoltage = (float) (batteryVol * 0.001);
            binding.voltageTv.setText(fullVoltage + " V");

            //battery health
            deviceHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            if (deviceHealth == BatteryManager.BATTERY_HEALTH_COLD) {
                binding.healthTv.setText("Cold");
            }
            if (deviceHealth == BatteryManager.BATTERY_HEALTH_DEAD) {
                binding.healthTv.setText("Dead");
            }
            if (deviceHealth == BatteryManager.BATTERY_HEALTH_GOOD) {
                binding.healthTv.setText("Good");
            }
            if (deviceHealth == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                binding.healthTv.setText("OverHeat");
            }
            if (deviceHealth == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                binding.healthTv.setText("Over voltage");
            }
            if (deviceHealth == BatteryManager.BATTERY_HEALTH_UNKNOWN) {
                binding.healthTv.setText("Unknown");
            }
            if (deviceHealth == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                binding.healthTv.setText("Unspecified Failure");
            }

            //Technology
            boolean isPresent = intent.getBooleanExtra("present", false);
            String technology = intent.getStringExtra("technology");
            int status = intent.getIntExtra("status", 0);
            int scale = intent.getIntExtra("scale", -1);
            int rawlevel = intent.getIntExtra("level", -1);
            int level = 0;

            Bundle bundle = intent.getExtras();

            Log.i("BatteryLevel", bundle.toString());

            if (isPresent) {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }

                String info = "";
                info += (technology + "\n\n");

//                setBatteryLevelText(info + "\n\n");
            } else {
//                setBatteryLevelText("Battery not present!!!");
            }

            //Charge Type
            if (isPresent) {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }

                String info1 = "";
                info1 += (getStatusString(status) + "\n\n");

//                setBatteryLevelText1(info1 + "\n\n");
            } else {
//                setBatteryLevelText1("Battery not present!!!");
            }


        }
    };
    private SharePreferences sharePreferences;

    public InformationFragment(Activity activity, TabLayout.Tab secondTab) {
        this.activity = activity;
        this.secondTab = secondTab;
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
        binding = FragmentInformationBinding.inflate(inflater, container, false);

        sharePreferences = new SharePreferences(requireActivity());

        isSwitch1On = sharePreferences.getBoolean(Constants.isSwitch1On);
        isSwitch2On = sharePreferences.getBoolean(Constants.isSwitch2On);

        if (isSwitch1On) {
            binding.switch1.setImageResource(R.drawable.on);
        } else {
            binding.switch1.setImageResource(R.drawable.off);
        }

        if (isSwitch2On) {
            binding.switch2.setImageResource(R.drawable.on);
        } else {
            binding.switch2.setImageResource(R.drawable.off);
        }

        if (sharePreferences.getInt(Constants.CHARGING_COMPLETE) == -1) {
            sharePreferences.putInt(Constants.CHARGING_COMPLETE, 80);
        } else {
            binding.seekbar.setProgress(sharePreferences.getInt(Constants.CHARGING_COMPLETE));
            binding.chargingCompleteTv.setText(getResources().getString(R.string.notify_me_to_stop_charging_when_the_battery_nreaches_80)+ " " + sharePreferences.getInt(Constants.CHARGING_COMPLETE) + "%");
        }
        if (sharePreferences.getInt(Constants.BATTERY_LOW) == -1) {
            sharePreferences.putInt(Constants.BATTERY_LOW, 20);
        }else {
            binding.seekbar2.setProgress(sharePreferences.getInt(Constants.BATTERY_LOW));
            binding.chargingRemindTv.setText(getResources().getString(R.string.notify_me_to_stop_charging_when_the_battery_nreaches_80) + " "+ sharePreferences.getInt(Constants.BATTERY_LOW) +  "%");
        }

        binding.designTv.setText(String.valueOf(getBatteryCapacity(requireActivity())));

        TabLayout tabLayout = activity.findViewById(R.id.tabLayout);

        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        requireActivity().registerReceiver(broadcastreceiver, intentfilter);

        binding.calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                        tabLayout.selectTab(secondTab);
                });

            }
        });

        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(
                    SeekBar seekBar,
                    int progress,
                    boolean fromUser) {

                // increment 1 in progress and
                // increase the textsize
                // with the value of progress
                binding.chargingCompleteTv.setText(getResources().getString(R.string.notify_me_to_stop_charging_when_the_battery_nreaches_80) + " " + progress + "%");
                sharePreferences.putInt(Constants.CHARGING_COMPLETE, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                // This method will automatically
                // called when the user touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                // This method will automatically
                // called when the user
                // stops touching the SeekBar
            }
        });
        binding.seekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(
                    SeekBar seekBar,
                    int progress,
                    boolean fromUser) {

                // increment 1 in progress and
                // increase the textsize
                // with the value of progress
                binding.chargingRemindTv.setText(getResources().getString(R.string.notify_me_to_stop_charging_when_the_battery_nreaches_80) + " " + progress + "%");
                sharePreferences.putInt(Constants.BATTERY_LOW, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                // This method will automatically
                // called when the user touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                // This method will automatically
                // called when the user
                // stops touching the SeekBar
            }
        });

        binding.switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSwitch1On) {
                    isSwitch1On = false;
                    sharePreferences.putBoolean(Constants.isSwitch1On, isSwitch1On);
                    binding.switch1.setImageResource(R.drawable.off);
                } else {
                    isSwitch1On = true;
                    sharePreferences.putBoolean(Constants.isSwitch1On, isSwitch1On);
                    binding.switch1.setImageResource(R.drawable.on);
                }
            }
        });

        binding.switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSwitch2On) {
                    isSwitch2On = false;
                    sharePreferences.putBoolean(Constants.isSwitch2On, isSwitch2On);
                    binding.switch2.setImageResource(R.drawable.off);
                } else {
                    isSwitch2On = true;
                    sharePreferences.putBoolean(Constants.isSwitch2On, isSwitch2On);
                    binding.switch2.setImageResource(R.drawable.on);
                }
            }
        });

        return binding.getRoot();
    }

    public long getBatteryCapacity(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            Integer chargeCounter = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Integer capacity = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter == Integer.MIN_VALUE || capacity == Integer.MIN_VALUE)
                return 0;

            return (chargeCounter / capacity) * 100;
        }
        return 0;
    }

    private String getStatusString(int status) {
        String statusString = "Unknown";

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "Not Charging";
                break;
        }

        return statusString;
    }
//
//    private void setBatteryLevelText1(String text) {
//        charge_type_degree.setText(text);
//    }
//
//    private void setBatteryLevelText(String text) {
//        technology_degree.setText(text);
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(broadcastreceiver);
    }

}