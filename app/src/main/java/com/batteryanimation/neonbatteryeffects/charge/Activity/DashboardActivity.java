package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;
import static com.batteryanimation.neonbatteryeffects.charge.Utils.showRateApp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.batteryanimation.neonbatteryeffects.charge.Fragment.AnimationFragment;
import com.batteryanimation.neonbatteryeffects.charge.Fragment.BatteryFragment;
import com.batteryanimation.neonbatteryeffects.charge.Fragment.HomeFragment;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private static final String SELECTED_ITEM_ID = "SELECTED_ITEM_ID";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_OVERLAY_PERMISSION = 100;
    private static Uri selectedImageUri = null;
    ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = getResources().getDrawable(R.drawable.splashbg);
        getWindow().setBackgroundDrawable(background);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();

//        loadFragment(new HomeFragment(DashboardActivity.this));
//        loadFragment(new HomeFragment());
//        loadFragment(HomeFragment.newInstance("home"));
        handleNavigtion();

        openGallery();

        binding.bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.battery_btm_nav) {
                    selectedFragment = new BatteryFragment(DashboardActivity.this);
                    binding.batteryBtm.setBackgroundResource(R.drawable.blur);
                    binding.batteryBtm.setTextColor(getResources().getColor(R.color.white));
                    binding.batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                    binding.homeBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                    binding.homeBtm.setTextColor(getResources().getColor(R.color.grey));
                    binding.homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                    binding.animationBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                    binding.animationBtm.setTextColor(getResources().getColor(R.color.grey));
                    binding.animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                } else if (itemId == R.id.home_btm_nav) {
                    selectedFragment = new HomeFragment(DashboardActivity.this);
                    binding.homeBtm.setBackgroundResource(R.drawable.blur);
                    binding.homeBtm.setTextColor(getResources().getColor(R.color.white));
                    binding.homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                    binding.batteryBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                    binding.batteryBtm.setTextColor(getResources().getColor(R.color.grey));
                    binding.batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                    binding.animationBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                    binding.animationBtm.setTextColor(getResources().getColor(R.color.grey));
                    binding.animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                } else if (itemId == R.id.animation_btm_nav) {
                    selectedFragment = new AnimationFragment();
                    binding.animationBtm.setBackgroundResource(R.drawable.blur);
                    binding.animationBtm.setTextColor(getResources().getColor(R.color.white));
                    binding.animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                    binding.homeBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                    binding.homeBtm.setTextColor(getResources().getColor(R.color.grey));
                    binding.homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                    binding.batteryBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                    binding.batteryBtm.setTextColor(getResources().getColor(R.color.grey));
                    binding.batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                }
                // It will help to replace the
                // one fragment to other.
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, selectedFragment).addToBackStack(null).commit();
                }
                return true;
            }
        });

        binding.bottomNav.setSelectedItemId(R.id.home_btm_nav);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void handleNavigtion() {
        View headerLayout = binding.navigationView.getHeaderView(0);
        ImageView closeDrawer = headerLayout.findViewById(R.id.close_drawer);
        TextView battery_nav = headerLayout.findViewById(R.id.battery_nav);
        TextView animation_nav = headerLayout.findViewById(R.id.animation_nav);
        TextView downloads_nav = headerLayout.findViewById(R.id.downloads_nav);
        TextView fav_nav = headerLayout.findViewById(R.id.fav_nav);
        TextView pp_nav = headerLayout.findViewById(R.id.pp_nav);
        TextView tu_nav = headerLayout.findViewById(R.id.tu_nav);

        closeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawLay.closeDrawer(GravityCompat.START);
            }
        });

        battery_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                        binding.drawLay.closeDrawer(GravityCompat.START);
//                        binding.batteryBtm.setBackgroundResource(R.drawable.blur);
//                        binding.batteryBtm.setTextColor(getResources().getColor(R.color.white));
//                        binding.batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
//                        binding.homeBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        binding.homeBtm.setTextColor(getResources().getColor(R.color.grey));
//                        binding.homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
//                        binding.animationBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        binding.animationBtm.setTextColor(getResources().getColor(R.color.grey));
//                        binding.animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
//                        loadFragment(BatteryFragment.newInstance("battery"));
//                        loadFragment(new BatteryFragment(DashboardActivity.this));
//                        loadFragment(new BatteryFragment());
                        binding.bottomNav.setSelectedItemId(R.id.battery_btm_nav);
                });

            }
        });

        animation_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                        binding.drawLay.closeDrawer(GravityCompat.START);
//                        binding.animationBtm.setBackgroundResource(R.drawable.blur);
//                        binding.animationBtm.setTextColor(getResources().getColor(R.color.white));
//                        binding.animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
//                        binding.homeBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        binding.homeBtm.setTextColor(getResources().getColor(R.color.grey));
//                        binding.homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
//                        binding.batteryBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        binding.batteryBtm.setTextColor(getResources().getColor(R.color.grey));
//                        binding.batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                        binding.bottomNav.setSelectedItemId(R.id.animation_btm_nav);
//                        loadFragment(new AnimationFragment());
//                        loadFragment(new AnimationFragment(DashboardActivity.this));
//                        loadFragment(AnimationFragment.newInstance("animation"));
                });
            }
        });

        downloads_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                        binding.drawLay.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(getApplicationContext(), DownloadsActivity.class));
                });
            }
        });

        fav_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                        binding.drawLay.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(getApplicationContext(), FavouritesActivity.class));
                });
            }
        });

        tu_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                        binding.drawLay.closeDrawer(GravityCompat.START);
                        startActivity(new Intent(getApplicationContext(), TermsOfUse.class).putExtra("activity", "db"));
                });
            }
        });

        pp_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://digitalcanvasstudios.blogspot.com/p/privacy-policy.html");
            }
        });

    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private boolean hasReadExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Overlay Permission Required!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
                return;
            }
        }

//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
//            Intent nextActivityIntent = new Intent(this, LoadActivity.class);
//            nextActivityIntent.putExtra("imageUri", selectedImageUri.toString());
//            startActivity(nextActivityIntent);
        }
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Overlay permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
//            Intent nextActivityIntent = new Intent(this, LoadActivity.class);
//            nextActivityIntent.putExtra("imageUri", selectedImageUri.toString());
//            startActivity(nextActivityIntent);
        }
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, fragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            exitDialog();
        } else {
            AdUtils.showBackPressAd(activity,isLoaded -> {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    overridePendingTransition(0, 0);
            });
        }
    }

    private void exitDialog() {

        Dialog dialog = new Dialog(DashboardActivity.this, R.style.SheetDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(this);

        View lay = inflater.inflate(R.layout.exit_dialog, null);
        TextView goback, exit, rate_txt, do_you;
        ImageView rateUs;
        RelativeLayout exit_bg;
        goback = lay.findViewById(R.id.goback);
        exit = lay.findViewById(R.id.exit);
        rateUs = lay.findViewById(R.id.rate_us);
        rate_txt = lay.findViewById(R.id.rate_txt);

        dialog.setContentView(lay);
//        AdUtils.showNativeAd(activity, Constants.adsResponseModel.getNative_ads().getAdx(), lay.findViewById(R.id.native_ad).findViewById(com.adsmodule.api.R.id.native_ad1), 1, null);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finishAffinity();
            }
        });

        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showRateApp(DashboardActivity.this);
            }
        });

        dialog.show();

    }

    public void batteryBtmClick(View view) {

        AdUtils.showInterstitialAd(activity, isLoaded -> {
                /*binding.batteryBtm.setBackgroundResource(R.drawable.blur);
                binding.batteryBtm.setTextColor(getResources().getColor(R.color.white));
                binding.batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                binding.homeBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                binding.homeBtm.setTextColor(getResources().getColor(R.color.grey));
                binding.homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                binding.animationBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                binding.animationBtm.setTextColor(getResources().getColor(R.color.grey));
                binding.animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
//                loadFragment(BatteryFragment.newInstance("battery"));
//                loadFragment(new BatteryFragment(DashboardActivity.this));
                loadFragment(new BatteryFragment());*/
                binding.bottomNav.setSelectedItemId(R.id.battery_btm_nav);
        });
    }

    public void homeBtmClick(View view) {

        AdUtils.showInterstitialAd(activity, isLoaded -> {
/*//                binding.homeBtm.setBackgroundResource(R.drawable.blur);
//                binding.homeBtm.setTextColor(getResources().getColor(R.color.white));
//                binding.homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
//                binding.batteryBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
//                binding.batteryBtm.setTextColor(getResources().getColor(R.color.grey));
//                binding.batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
//                binding.animationBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
//                binding.animationBtm.setTextColor(getResources().getColor(R.color.grey));
//                binding.animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new HomeFragment(DashboardActivity.this)).commit();
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
//                loadFragment(new HomeFragment(DashboardActivity.this));*/

                binding.bottomNav.setSelectedItemId(R.id.home_btm_nav);

        });
    }

    public void animationBtmClick(View view) {

        AdUtils.showInterstitialAd(activity, isLoaded -> {
               /* binding.animationBtm.setBackgroundResource(R.drawable.blur);
                binding.animationBtm.setTextColor(getResources().getColor(R.color.white));
                binding.animationBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                binding.homeBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                binding.homeBtm.setTextColor(getResources().getColor(R.color.grey));
                binding.homeBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));
                binding.batteryBtm.setBackgroundColor(getResources().getColor(R.color.transparent));
                binding.batteryBtm.setTextColor(getResources().getColor(R.color.grey));
                binding.batteryBtm.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.grey));

//                loadFragment(new AnimationFragment(DashboardActivity.this));
                loadFragment(new AnimationFragment());
//                loadFragment(AnimationFragment.newInstance("animation"));*/
                binding.bottomNav.setSelectedItemId(R.id.animation_btm_nav);

        });
    }
}