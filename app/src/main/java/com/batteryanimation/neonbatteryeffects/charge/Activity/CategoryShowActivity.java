package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.AppOpenAds.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adsmodule.api.AdsModule.AdUtils;
import com.adsmodule.api.AdsModule.Interfaces.AppInterfaces;
import com.adsmodule.api.AdsModule.Utils.Constants;
import com.batteryanimation.neonbatteryeffects.charge.BuildConfig;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SharedPreferencesUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryShowActivity extends AppCompatActivity {
    ImageView imageView,back;
    Bitmap photoBitmap;
    TextView setWallpaper, addToFavorites, saveBtn;

    public static final String FAVORITES_PREF_NAME_LIVE_CHARGE = "my_favorites_theme_charge";
    public static final String DOWNLOADS_PREF_NAME_LIVE_CHARGE = "my_downloads_theme_charge";

    private Set<String> favoritesSet, downloadset;
    private SharedPreferences sharedPreferences, sharedPreferences1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        setContentView(R.layout.activity_category_show);

        sharedPreferences = getSharedPreferences(FAVORITES_PREF_NAME_LIVE_CHARGE, Context.MODE_PRIVATE);
        sharedPreferences1 = getSharedPreferences(DOWNLOADS_PREF_NAME_LIVE_CHARGE, Context.MODE_PRIVATE);
        favoritesSet = sharedPreferences.getStringSet(FAVORITES_PREF_NAME_LIVE_CHARGE, new HashSet<>());
        downloadset = sharedPreferences1.getStringSet(DOWNLOADS_PREF_NAME_LIVE_CHARGE, new HashSet<>());

        Intent intent = getIntent();

        imageView = findViewById(R.id.ivPreview);
        setWallpaper = findViewById(R.id.apply_btn);
        addToFavorites = findViewById(R.id.add_to_favorites);
        saveBtn = findViewById(R.id.save_btn);
//        back = findViewById(R.id.backbt);


//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               onBackPressed();
//            }
//        });
        String imageUrl = getIntent().getStringExtra("imageUrl");
        Glide.with(CategoryShowActivity.this)
                .load(imageUrl)
                .into(imageView);

        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String[] options = {"Home screen", "Lock screen", "Both"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoryShowActivity.this);
                    builder.setTitle("SET WALLPAPER AS");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new SetWallpaperTask().execute(which + 1);
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    new SetWallpaperTask().execute(0);
                    finish();
                }
            }
        });

        addToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, isLoaded -> {
                    if (favoritesSet.contains(imageUrl)) {
                        favoritesSet.remove(imageUrl);
                    } else {
                        favoritesSet.add(imageUrl);
                    }
                    // Save the updated favorites set
                    try {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(FAVORITES_PREF_NAME_LIVE_CHARGE, favoritesSet);
                        editor.apply();
                        Toast.makeText(CategoryShowActivity.this, "Added to favourites!!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        /*throw new RuntimeException(e);*/
                        Toast.makeText(CategoryShowActivity.this, "Some error occurred!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, isLoaded -> {
                    if (downloadset.contains(imageUrl)) {
                        downloadset.remove(imageUrl);
                    } else {
                        downloadset.add(imageUrl);
                    }
                    saveBtn.setEnabled(false);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putStringSet(DOWNLOADS_PREF_NAME_LIVE_CHARGE, downloadset);
                    editor.apply();
                    downloadImageToGallery(imageUrl);
                });
            }
        });
    }

    private void downloadImageToGallery(String imageUrl) {
        /*Glide.with(this)
                .asGif()
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(new SimpleTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                        saveGifToGallery(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toast.makeText(getApplicationContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
                    }
                });*/

        try {
            ExecutorService executors = Executors.newSingleThreadExecutor();
            executors.submit(() -> {
                Glide.with(this)
                        .download(imageUrl)
                        .listener(new RequestListener<File>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Error saving", Toast.LENGTH_SHORT).show();
                                });

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                                //                        progressDailog.dismiss();
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Image saved!!", Toast.LENGTH_SHORT).show();
                                    try {
                                        saveGifImage(getApplicationContext(), getBytesFromFile(resource), "BatteryAnimation" + System.currentTimeMillis() + ".png" /*createName(url)*/);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                return true;
                            }
                        }).submit();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;
    }

    public void saveGifImage(Context context, byte[] bytes, String imgName) {
        FileOutputStream fos = null;
        try {

            PackageManager m = getPackageManager();
            String s = getPackageName();
            try {
                PackageInfo p = m.getPackageInfo(s, 0);
                s = p.applicationInfo.dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("yourtag", "Error Package name not found ", e);
            }
            File customDownloadDirectory = new File(s);
//            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            File customDownloadDirectory = new File(externalStoragePublicDirectory, getPackageName());
            if (!customDownloadDirectory.exists()) {
                boolean isFileMade = customDownloadDirectory.mkdirs();
            }
            if (customDownloadDirectory.exists()) {
                File file = new File(customDownloadDirectory, imgName);
                fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.flush();
                fos.close();
                if (file != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, file.getName());
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
                    values.put(MediaStore.Images.Media.DESCRIPTION, "");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
                    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());

                    ContentResolver contentResolver = context.getContentResolver();
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Toast.makeText(context, "Image saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class SetWallpaperTask extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... integers) {
            WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (integers[0] == 1) {
                    try {
                        manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (integers[0] == 2) {
                    try {
                        manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (integers[0] == 0) {
                    try {
                        manager.setBitmap(photoBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        AdUtils.showBackPressAds(activity, Constants.adsResponseModel.getApp_open_ads().getAdx(), new AppInterfaces.AppOpenADInterface() {
            @Override
            public void appOpenAdState(boolean state_load) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

    }
}
