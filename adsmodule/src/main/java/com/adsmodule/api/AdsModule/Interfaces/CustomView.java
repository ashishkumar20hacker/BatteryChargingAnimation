package com.adsmodule.api.AdsModule.Interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adsmodule.api.AdsModule.AdUtils;
import com.adsmodule.api.AdsModule.Utils.Constants;
import com.adsmodule.api.R;
import com.google.android.material.card.MaterialCardView;

public class CustomView extends MaterialCardView {


    private int adType;
    private int borderColor;
    private Drawable adPlaceHolder;

    public CustomView(@NonNull Context context) {
        super(context);
    }

    public CustomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);
        try {
            adType = Integer.parseInt(array.getString(R.styleable.CustomView_adType));
            borderColor = array.getColor(R.styleable.CustomView_borderColor, Color.TRANSPARENT);
            adPlaceHolder = array.getDrawable(R.styleable.CustomView_placeholder);

        } finally {
            array.recycle();
        }
        this.setRadius(0f);
        this.setStrokeWidth(0);
        this.setStrokeColor(borderColor);
        AdUtils.showNativeAd((Activity) context, Constants.adsResponseModel.getNative_ads().getAdx(), this, adType, adPlaceHolder == null ? null : adPlaceHolder);

    }


}
