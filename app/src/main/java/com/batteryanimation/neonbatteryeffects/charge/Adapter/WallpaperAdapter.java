package com.batteryanimation.neonbatteryeffects.charge.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.batteryanimation.neonbatteryeffects.charge.Activity.AnimationPreviewActivity;
import com.batteryanimation.neonbatteryeffects.charge.Activity.CategoryShowActivity;
import com.batteryanimation.neonbatteryeffects.charge.Model.LockThemeModel;
import com.batteryanimation.neonbatteryeffects.charge.R;
import com.batteryanimation.neonbatteryeffects.charge.SetWallPaperListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {

    Context context;

    List<LockThemeModel> lockThemeModelArrayList;
    int i = 0;
    public WallpaperAdapter(Context context, List<LockThemeModel> lockThemeModelArrayList, int i) {
        this.context = context;
        this.lockThemeModelArrayList = lockThemeModelArrayList;
        this.i = i;
    }

    @NonNull
    @Override
    public WallpaperAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animation_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperAdapter.ViewHolder holder, int position) {
        LockThemeModel lockThemeModel = lockThemeModelArrayList.get(position);

        holder.delete.setVisibility(View.GONE);

        Glide.with(context).load(lockThemeModelArrayList.get(position).getImageURL()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.gifimageView);

        holder.gifimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.onSelect(lockThemeModel.getImageURL(), position);
//                AdUtils.showInterstitialAd(Constants.adsResponseModel.getInterstitial_ads().getAdx(), activity, isLoaded -> {
                Intent intent = new Intent(context, CategoryShowActivity.class);
                intent.putExtra("imageUrl", lockThemeModel.getImageURL());
                intent.putExtra("position", position);
//                intent.putStringArrayListExtra("favoritesSet", new ArrayList<>(favoritesSet));
//                intent.putStringArrayListExtra("downloadsSet", new ArrayList<>(downloadsSet));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
////                });

            }
        });

    }

    @Override
    public int getItemCount() {
        if (i == 0) {
            return lockThemeModelArrayList.size();
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
