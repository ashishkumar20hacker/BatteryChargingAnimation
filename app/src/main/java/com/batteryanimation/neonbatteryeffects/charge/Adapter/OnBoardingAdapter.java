package com.batteryanimation.neonbatteryeffects.charge.Adapter;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.batteryanimation.neonbatteryeffects.charge.Model.DataModel;
import com.batteryanimation.neonbatteryeffects.charge.databinding.ViewOnboardScreenBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Objects;

public class OnBoardingAdapter extends ListAdapter<DataModel, OnBoardingAdapter.ViewHolder> {

    static DiffUtil.ItemCallback<DataModel> diffCallback = new DiffUtil.ItemCallback<DataModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public OnBoardingAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewOnboardScreenBinding binding = ViewOnboardScreenBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel model = getItem(position);

        Glide.with(holder.itemView.getContext()).load(model.getIcon()).into(holder.binding.image);
        if (model.getGifImage() == 1) {
            holder.binding.imageView.setVisibility(View.GONE);
            holder.binding.lottie.setVisibility(View.VISIBLE);
            holder.binding.txt.setVisibility(View.VISIBLE);
        } else if (model.getGifImage() == 2) {
            holder.binding.imageView.setVisibility(View.GONE);
            holder.binding.lottie.setVisibility(View.GONE);
            holder.binding.txt.setVisibility(View.GONE);
        } else {
            Glide.with(holder.itemView.getContext()).load(model.getGifImage()).into(holder.binding.imageView);
            holder.binding.imageView.setVisibility(View.VISIBLE);
            holder.binding.lottie.setVisibility(View.GONE);
            holder.binding.txt.setVisibility(View.GONE);
        }
        holder.binding.title.setText(model.getName());
        LinearGradient shader = new
                LinearGradient(0f, 0f, 0f, holder.binding.title.getTextSize(), Color.parseColor("#63D0D0"), Color.parseColor("#0A99A4"), Shader.TileMode.CLAMP);
        holder.binding.title.getPaint().setShader(shader);


        holder.binding.desc.setText(model.getValue());

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewOnboardScreenBinding binding;

        public ViewHolder(@NonNull ViewOnboardScreenBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
