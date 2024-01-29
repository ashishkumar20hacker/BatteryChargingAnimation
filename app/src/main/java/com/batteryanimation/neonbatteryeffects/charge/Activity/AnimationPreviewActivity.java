package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

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
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.batteryanimation.neonbatteryeffects.charge.BuildConfig;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SharePreferences;
import com.batteryanimation.neonbatteryeffects.charge.SharedPreferencesUtil;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ActivityAnimationPreviewBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimationPreviewActivity extends AppCompatActivity {

    public static final String FAVORITES_PREF_NAME_LIVE_CHARGE = "my_favorites_theme_charge";
    public static final String DOWNLOADS_PREF_NAME_LIVE_CHARGE = "my_downloads_theme_charge";
    ActivityAnimationPreviewBinding binding;
    SharedPreferencesUtil sharedPreferencesUtil;
    int gifSelected;
    Bitmap photoBitmap;
    String load;
    private String imageUrl;
    private Set<String> favoritesSet, downloadset;
    private SharedPreferences sharedPreferences, sharedPreferences1;

    SharePreferences sharePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        binding = ActivityAnimationPreviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        gifSelected = getIntent().getIntExtra("animationName", 0);
        sharedPreferencesUtil = new SharedPreferencesUtil(this);
        sharedPreferences = getSharedPreferences(FAVORITES_PREF_NAME_LIVE_CHARGE, Context.MODE_PRIVATE);
        sharedPreferences1 = getSharedPreferences(DOWNLOADS_PREF_NAME_LIVE_CHARGE, Context.MODE_PRIVATE);
        favoritesSet = sharedPreferences.getStringSet(FAVORITES_PREF_NAME_LIVE_CHARGE, new HashSet<>());
        downloadset = sharedPreferences1.getStringSet(DOWNLOADS_PREF_NAME_LIVE_CHARGE, new HashSet<>());
        imageUrl = getIntent().getStringExtra("imageUrlCharge");

        sharePreferences = new SharePreferences(this);

        load = getIntent().getStringExtra("load");

        if (getIntent().getBooleanExtra("show", true)) {
            binding.addToFavorites.setVisibility(View.VISIBLE);
            binding.saveBtn.setVisibility(View.VISIBLE);
        } else {
            binding.addToFavorites.setVisibility(View.GONE);
            binding.saveBtn.setVisibility(View.GONE);
        }

        if (imageUrl.contains("idecloudstoragepanel")) {
            Glide.with(AnimationPreviewActivity.this)
                    .asGif()
                    .load(imageUrl)
                    .into(binding.smallAnim);
        } else {
            Glide.with(AnimationPreviewActivity.this)
                    .asGif()
                    .load(imageUrl)
                    .into(binding.ivPreview);
        }

        binding.applyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (load.equals("Animation")) {
                    AdUtils.showInterstitialAd(activity, isLoaded -> {
                        setAnimation();
                    });
                } else {
                    setWallpapers();
                }

            }
        });

        binding.addToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                    if (favoritesSet.contains(imageUrl)) {
                        favoritesSet.remove(imageUrl);
                        binding.addToFavorites.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                    } else {
                        favoritesSet.add(imageUrl);
                        binding.addToFavorites.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                    }
                    // Save the updated favorites set
                    try {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(FAVORITES_PREF_NAME_LIVE_CHARGE, favoritesSet);
                        editor.apply();
                        Toast.makeText(AnimationPreviewActivity.this, "Added to favourites!!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        /*throw new RuntimeException(e);*/
                        Toast.makeText(AnimationPreviewActivity.this, "Some error occurred!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdUtils.showInterstitialAd(activity, isLoaded -> {
                    if (downloadset.contains(imageUrl)) {
                        downloadset.remove(imageUrl);
                        binding.saveBtn.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                    } else {
                        downloadset.add(imageUrl);
                        binding.saveBtn.getCompoundDrawables()[1].setTint(getResources().getColor(R.color.white));
                    }
                    binding.saveBtn.setEnabled(false);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putStringSet(DOWNLOADS_PREF_NAME_LIVE_CHARGE, downloadset);
                    editor.apply();
                    downloadImageToGallery(imageUrl);
                });
            }
        });

    }

    private void setWallpapers() {
        photoBitmap = ((BitmapDrawable) binding.ivPreview.getDrawable()).getBitmap();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String[] options = {"Home screen", "Lock screen", "Both"};
            AlertDialog.Builder builder = new AlertDialog.Builder(AnimationPreviewActivity.this);
            builder.setTitle("SET WALLPAPER AS");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new SetWallpaperTask().execute(which + 1);

                }
            });
            builder.show();
        } else {
            new SetWallpaperTask().execute(0);
        }
    }

    private void setAnimation() {
        AnimationPreviewActivity.this.sharedPreferencesUtil.isVideo(false);
        AnimationPreviewActivity.this.sharedPreferencesUtil.setAnimationName(AnimationPreviewActivity.this.gifSelected);
        SharedPreferences.Editor editor = getSharedPreferences("MyOtherChargePreferences", Context.MODE_PRIVATE).edit();
        editor.putString("imageUrlChargeOther", AnimationPreviewActivity.this.imageUrl);
        editor.apply();
        Toast.makeText(AnimationPreviewActivity.this, "Applied Successfully!!", Toast.LENGTH_SHORT).show();
        sharePreferences.putBoolean(com.batteryanimation.neonbatteryeffects.charge.Constants.isAnimationSet, true);
        AnimationPreviewActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        AdUtils.showBackPressAd(activity, isLoaded -> {
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
//                AnimationPreviewActivity.super.onBackPressed();
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
                                        saveGifImage(getApplicationContext(), getBytesFromFile(resource), "BatteryAnimation" + System.currentTimeMillis() + ".gif" /*createName(url)*/);
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

                    Toast.makeText(context, "Image saved " /*+ file.getAbsolutePath()*/, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveGifToGallery(GifDrawable gifDrawable) {
        ByteBuffer byteBuffer = gifDrawable.getBuffer();
        if (byteBuffer == null) {

            return;
        }

        // Convert ByteBuffer to byte array
        byte[] gifBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(gifBytes);

        // Get the directory to save the GIF
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + getResources().getString(R.string.app_name));
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Toast.makeText(getApplicationContext(), "Failed to create directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String fileName = System.currentTimeMillis() + ".gif";
        File gifFile = new File(directory, fileName);

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(gifFile);

            outputStream.write(gifBytes);
            Toast.makeText(getApplicationContext(), "GIF saved successfully", Toast.LENGTH_SHORT).show();

            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{gifFile.getAbsolutePath()}, new String[]{"image/gif"}, null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed to save GIF", Toast.LENGTH_SHORT).show();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class SetWallpaperTask extends AsyncTask<Integer, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... integers) {
            WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (integers[0] == 1) {
                    runOnUiThread(() -> {
                        try {
                            manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                            Toast.makeText(AnimationPreviewActivity.this, "Applied Successfully!!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else if (integers[0] == 2) {
                    runOnUiThread(() -> {
                        try {
                            manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_LOCK);//For Lock screen
                            Toast.makeText(AnimationPreviewActivity.this, "Applied Successfully!!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        try {
                            manager.setBitmap(photoBitmap, null, true, WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
                            Toast.makeText(AnimationPreviewActivity.this, "Applied Successfully!!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                if (integers[0] == 0) {
                    runOnUiThread(() -> {
                        try {
                            manager.setBitmap(photoBitmap);
                            Toast.makeText(AnimationPreviewActivity.this, "Applied Successfully!!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            finish();
        }
    }


}