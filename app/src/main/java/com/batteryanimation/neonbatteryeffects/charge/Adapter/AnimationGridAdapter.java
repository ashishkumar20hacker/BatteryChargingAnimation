package com.batteryanimation.neonbatteryeffects.charge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.batteryanimation.neonbatteryeffects.charge.Activity.AnimationPreviewActivity;
import com.batteryanimation.neonbatteryeffects.charge.Model.Wallpaper;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class AnimationGridAdapter extends RecyclerView.Adapter<AnimationGridAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Wallpaper> arrayList;
    int i = 0;

    public AnimationGridAdapter(Context context, ArrayList<Wallpaper> arrayList, int i) {
        this.context = context;
        this.arrayList = arrayList;
        this.i = i;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animation_grid_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wallpaper wallpaper = arrayList.get(position);
        holder.delete.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE); // Show progress bar
        Glide.with(context)
                .asGif()
                .load(wallpaper.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE); // Hide progress bar on load failure
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE); // Hide progress bar on successful load
                        return false;
                    }
                })
                .into(holder.gifimageView);

        holder.gifimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, isLoaded -> {
                Intent intent = new Intent(context, AnimationPreviewActivity.class);
                intent.putExtra("imageUrlCharge", wallpaper.getUrl());
                intent.putExtra("position", position);
                intent.putExtra("load", "Animation");
//                    intent.putStringArrayListExtra("favoritesSet", new ArrayList<>(favoritesSet));
//                    intent.putStringArrayListExtra("downloadsSet", new ArrayList<>(downloadset));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
//                });

            }
        });

    }

    @Override
    public int getItemCount() {
        if (i == 0) {
            return arrayList.size();
        } else {
            return i;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        GifImageView progressBar;
        ImageView delete, gifimageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gifimageView = itemView.findViewById(R.id.gifimageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
