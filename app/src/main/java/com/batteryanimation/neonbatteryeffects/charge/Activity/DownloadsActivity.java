package com.batteryanimation.neonbatteryeffects.charge.Activity;

import static com.batteryanimation.neonbatteryeffects.charge.SingletonClasses.LifeCycleOwner.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adsmodule.api.adsModule.utils.AdUtils;
import com.batteryanimation.neonbatteryeffects.charge.Adapter.FavouritesLiveAdapter;
import com.batteryanimation.neonbatteryeffects.charge.BuildConfig;
import com.batteryanimation.neonbatteryeffects.charge.Model.Wallpaper;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ActivityDownloadsBinding;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;

public class DownloadsActivity extends AppCompatActivity {

    ActivityDownloadsBinding binding;
    ArrayList<File_Model> img_path;
    CreationAdapter creationAdapter;

    private FavouritesLiveAdapter favouritesAdapter;
    private ArrayList<Wallpaper> favoriteDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = getResources().getDrawable(R.drawable.splashbg);
        getWindow().setBackgroundDrawable(background);
        binding = ActivityDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.downloadsRv.setLayoutManager(new GridLayoutManager(this, 3));
        new LoadImages().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

       /* favoriteDataList = loadFavoriteDataList();
        if (favoriteDataList.isEmpty()){
            binding.empty.setVisibility(View.VISIBLE);
        } else {
            binding.empty.setVisibility(View.GONE);
            binding.downloadsRv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
            favouritesAdapter = new FavouritesLiveAdapter(DownloadsActivity.this, favoriteDataList);
            binding.downloadsRv.setAdapter(favouritesAdapter);
        }*/

        binding.backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        AdUtils.showBackPressAd(activity, isLoaded -> {
                DownloadsActivity.super.onBackPressed();
        });

    }

    private ArrayList<Wallpaper> loadFavoriteDataList() {
        ArrayList<Wallpaper> favoriteList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(AnimationPreviewActivity.DOWNLOADS_PREF_NAME_LIVE_CHARGE, Context.MODE_PRIVATE);
        Set<String> favoritesSet = sharedPreferences.getStringSet(AnimationPreviewActivity.DOWNLOADS_PREF_NAME_LIVE_CHARGE, new HashSet<>());
        for (String imageUrl : favoritesSet) {
            Wallpaper wallpaper = new Wallpaper(imageUrl);
            favoriteList.add(wallpaper);
        }

        return favoriteList;
    }

    private void updateFileList() {

        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }
        String path = s /*+ BuildConfig.APPLICATION_ID*/;
//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + getResources().getString(R.string.app_name);
        File directory = new File(path);
//        File[] files = directory.listFiles();
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("BatteryAnimation");
            }
        });
        img_path = new ArrayList<>();
        Comparator<File> fileDateCmp = new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                // Compare the last modified date of the files
                long lastModified1 = o1.lastModified();
                long lastModified2 = o2.lastModified();
                if (lastModified1 > lastModified2) {
                    return -1; // o1 is newer than o2, so return -1 to place it first
                } else if (lastModified1 < lastModified2) {
                    return 1; // o1 is older than o2, so return 1 to place it after o2
                } else {
                    return 0; // o1 and o2 have the same last modified date, so return 0
                }
            }
        };

        if (files != null) {
            Arrays.sort(files, fileDateCmp);

            for (int i = 0; i < files.length; i++) {
                File_Model file_model = new File_Model();
                file_model.file_path = files[i].getAbsolutePath();
                file_model.file_title = files[i].getName();
                img_path.add(file_model);
            }
        }

    }

    private class LoadImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            updateFileList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            if (img_path.size() == 0) {
//                rlsph.setVisibility(View.VISIBLE);
//            } else {
//                rlsph.setVisibility(View.GONE);
            if (img_path.size() != 0) {
                binding.downloadsRv.setVisibility(View.VISIBLE);
                binding.empty.setVisibility(View.GONE);
                creationAdapter = new CreationAdapter(img_path);
                binding.downloadsRv.setAdapter(creationAdapter);
                creationAdapter.notifyDataSetChanged();
            } else {
                binding.downloadsRv.setVisibility(View.GONE);
                binding.empty.setVisibility(View.VISIBLE);
            }
//            }
        }
    }

    private class File_Model {
        String file_path;
        String file_title;
    }

    private class CreationAdapter extends RecyclerView.Adapter<CreationAdapter.ViewHolder> {
        ArrayList<File_Model> paths;
        public CreationAdapter(ArrayList<File_Model> imgPath) {
            this.paths = imgPath;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animation_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Glide.with(getApplicationContext()).load(paths.get(position).file_path).into(holder.gifimageView);

            holder.progressBar.setVisibility(View.GONE);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DownloadsActivity.this, R.style.MyDialogTheme);
                    builder.setMessage((Html.fromHtml("<font color='#FFFFFF'>Are you sure you want to delete?</font>")));

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String filepath = paths.get(position).file_path;
                            if (new File(filepath).delete()) {
                                paths.remove(position);
                                creationAdapter.notifyDataSetChanged();
                            }
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (paths.get(position).file_path.contains(".gif")) {
                        Intent intent = new Intent(getApplicationContext(), AnimationPreviewActivity.class);
                        intent.putExtra("imageUrlCharge", paths.get(position).file_path);
                        intent.putExtra("position", position);
                        intent.putExtra("show", false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CategoryShowActivity.class);
                        intent.putExtra("imageUrl", paths.get(position).file_path);
                        intent.putExtra("position", position);
                        intent.putExtra("show", false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return img_path.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            GifImageView progressBar;
            ImageView gifimageView,delete;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                gifimageView = itemView.findViewById(R.id.gifimageView);
                progressBar = itemView.findViewById(R.id.progressBar);
                delete = itemView.findViewById(R.id.delete);
            }
        }
    }
}