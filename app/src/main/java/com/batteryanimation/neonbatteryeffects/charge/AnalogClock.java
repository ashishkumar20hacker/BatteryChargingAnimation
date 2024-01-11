package com.batteryanimation.neonbatteryeffects.charge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AnalogClock extends View {
    private final Runnable mClockTick;
    private String mDescFormat;
    private Drawable mDial;

    public boolean mEnableSeconds;
    private Drawable mHourHand;
    private final BroadcastReceiver mIntentReceiver;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;

    public Calendar mTime;

    public TimeZone mTimeZone;

    public AnalogClock(Context context) {
        this(context, (AttributeSet) null);
    }

    public AnalogClock(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AnalogClock(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (AnalogClock.this.mTimeZone == null && "android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction())) {
                    Calendar unused = AnalogClock.this.mTime = Calendar.getInstance(TimeZone.getTimeZone(intent.getStringExtra("time-zone")));
                }
                AnalogClock.this.onTimeChanged();
            }
        };
        this.mClockTick = new Runnable() {
            public void run() {
                AnalogClock.this.onTimeChanged();
                if (AnalogClock.this.mEnableSeconds) {
                    AnalogClock.this.postDelayed(this, 1000 - (System.currentTimeMillis() % 1000));
                }
            }
        };
        this.mEnableSeconds = true;
        Resources resources = context.getResources();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AnalogClock);
        this.mTime = Calendar.getInstance();
        this.mDescFormat = ((SimpleDateFormat) DateFormat.getTimeFormat(context)).toLocalizedPattern();
        this.mEnableSeconds = obtainStyledAttributes.getBoolean(4, true);
        Drawable drawable = obtainStyledAttributes.getDrawable(0);
        this.mDial = drawable;
        if (drawable == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.mDial = context.getDrawable(R.drawable.dial);
            } else {
                this.mDial = resources.getDrawable(R.drawable.dial);
            }
        }
        Drawable drawable2 = obtainStyledAttributes.getDrawable(1);
        this.mHourHand = drawable2;
        if (drawable2 == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.mHourHand = context.getDrawable(R.drawable.hour);
            } else {
                this.mHourHand = resources.getDrawable(R.drawable.hour);
            }
        }
        Drawable drawable3 = obtainStyledAttributes.getDrawable(2);
        this.mMinuteHand = drawable3;
        if (drawable3 == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.mMinuteHand = context.getDrawable(R.drawable.minute);
            } else {
                this.mMinuteHand = resources.getDrawable(R.drawable.minute);
            }
        }
        Drawable drawable4 = obtainStyledAttributes.getDrawable(3);
        this.mSecondHand = drawable4;
        if (drawable4 == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.mSecondHand = context.getDrawable(R.drawable.second);
            } else {
                this.mSecondHand = resources.getDrawable(R.drawable.second);
            }
        }
        initDrawable(context, this.mDial);
        initDrawable(context, this.mHourHand);
        initDrawable(context, this.mMinuteHand);
        initDrawable(context, this.mSecondHand);
    }


    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getContext().registerReceiver(this.mIntentReceiver, intentFilter);
        TimeZone timeZone = this.mTimeZone;
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        this.mTime = Calendar.getInstance(timeZone);
        onTimeChanged();
        if (this.mEnableSeconds) {
            this.mClockTick.run();
        }
    }


    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(this.mIntentReceiver);
        removeCallbacks(this.mClockTick);
    }


    public void onMeasure(int i, int i2) {
        setMeasuredDimension(getDefaultSize(Math.max(this.mDial.getIntrinsicWidth(), getSuggestedMinimumWidth()), i), getDefaultSize(Math.max(this.mDial.getIntrinsicHeight(), getSuggestedMinimumHeight()), i2));
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int save = canvas.save();
        canvas.translate((float) (width / 2), (float) (height / 2));
        float min = Math.min(((float) width) / ((float) this.mDial.getIntrinsicWidth()), ((float) height) / ((float) this.mDial.getIntrinsicHeight()));
        if (min < 1.0f) {
            canvas.scale(min, min, 0.0f, 0.0f);
        }
        this.mDial.draw(canvas);
        float f = ((float) this.mTime.get(10)) * 30.0f;
        canvas.rotate(f, 0.0f, 0.0f);
        this.mHourHand.draw(canvas);
        float f2 = ((float) this.mTime.get(12)) * 6.0f;
        canvas.rotate(f2 - f, 0.0f, 0.0f);
        this.mMinuteHand.draw(canvas);
        if (this.mEnableSeconds) {
            canvas.rotate((((float) this.mTime.get(13)) * 6.0f) - f2, 0.0f, 0.0f);
            this.mSecondHand.draw(canvas);
        }
        canvas.restoreToCount(save);
    }


    public boolean verifyDrawable(Drawable drawable) {
        return this.mDial == drawable || this.mHourHand == drawable || this.mMinuteHand == drawable || this.mSecondHand == drawable || super.verifyDrawable(drawable);
    }

    private void initDrawable(Context context, Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth() / 2;
        int intrinsicHeight = drawable.getIntrinsicHeight() / 2;
        drawable.setBounds(-intrinsicWidth, -intrinsicHeight, intrinsicWidth, intrinsicHeight);
    }


    public void onTimeChanged() {
        this.mTime.setTimeInMillis(System.currentTimeMillis());
        setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
        invalidate();
    }

    public void setTimeZone(String str) {
        TimeZone timeZone = TimeZone.getTimeZone(str);
        this.mTimeZone = timeZone;
        this.mTime.setTimeZone(timeZone);
        onTimeChanged();
    }
}
