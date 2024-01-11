package com.batteryanimation.neonbatteryeffects.charge.Adapter;


import static com.batteryanimation.neonbatteryeffects.charge.Activity.AnimationPreviewActivity.FAVORITES_PREF_NAME_LIVE_CHARGE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.batteryanimation.neonbatteryeffects.charge.Model.Wallpaper;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;

public class FavouritesLiveAdapter extends RecyclerView.Adapter<FavouritesLiveAdapter.ViewHolder> {
    private ArrayList<Wallpaper> favoriteDataList;
    private Context context;
    private Set<String> favoritesSet;
    private SharedPreferences sharedPreferences;

    public FavouritesLiveAdapter(Context context, ArrayList<Wallpaper> favoriteDataList) {
        this.context = context;
        this.favoriteDataList = favoriteDataList;
        sharedPreferences = context.getSharedPreferences(FAVORITES_PREF_NAME_LIVE_CHARGE, Context.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animation_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wallpaper wallpaper = favoriteDataList.get(position);

        favoritesSet = sharedPreferences.getStringSet(FAVORITES_PREF_NAME_LIVE_CHARGE, new HashSet<>());
        holder.gifImageView.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
        holder.relativeLayout.setVisibility(View.GONE);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

//        Glide.with(context)
//                .asGif()
//                .load(wallpaper.getUrl())
//                .apply(requestOptions)
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .into(holder.gifImageView);

        Glide.with(context).load(wallpaper.getUrl()).into(holder.gifImageView);

        holder.progressBar.setVisibility(View.GONE);
        holder.gifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, isLoaded -> {
//                Intent intent = new Intent(context, AnimationPreviewActivity.class);
//                intent.putExtra("imageUrlCharge", wallpaper.getUrl());
//                intent.putExtra("position", position);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//                });

            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
                    builder.setMessage((Html.fromHtml("<font color='#FFFFFF'>Are you sure you want to delete?</font>")));

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                String filepath = favoriteDataList.get(position).getUrl();
                                if (new File(filepath).delete()) {
                                    favoriteDataList.remove(position);
                                    if (favoritesSet.contains(filepath)) {
                                        favoritesSet.remove(filepath);
                                    }
                                    notifyDataSetChanged();
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
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.white));

            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gifImageView;
        ImageView relativeLayout;
        GifImageView progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gifImageView = itemView.findViewById(R.id.gifimageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            relativeLayout = itemView.findViewById(R.id.delete);
        }
    }
}
