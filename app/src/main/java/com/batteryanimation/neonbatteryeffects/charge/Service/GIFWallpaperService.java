package com.batteryanimation.neonbatteryeffects.charge.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class GIFWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        SharedPreferences sharedPref = getSharedPreferences("LiveWallpaper", Context.MODE_PRIVATE);
        String gifDataString = sharedPref.getString("live_wallpaper1", "");
        Log.e("hii","create"+gifDataString);
        return new GIFWallpaperEngine(gifDataString);
    }

    private class GIFWallpaperEngine extends Engine {
        private SurfaceHolder holder;
        private boolean visible;
        private Handler handler;
        private String gifDataString;

        public GIFWallpaperEngine(String gifDataString) {
            this.gifDataString = gifDataString;
            handler = new Handler();
        }

        private Runnable drawGIF = new Runnable() {
            public void run() {
                draw();
            }
        };

        private void draw() {
            if (visible && holder != null) {
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    if (canvas != null) {
                        canvas.drawColor(Color.BLACK); // Clear the canvas
                        // Load the animated GIF using Glide
                        Canvas finalCanvas = canvas;
                        Glide.with(getApplicationContext())
                                .asGif()
                                .load(gifDataString)
                                .listener(new RequestListener<GifDrawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                        String errorMessage = "Failed to load GIF: " + (e != null ? e.getMessage() : "Unknown error");
                                        Log.e("GIFWallpaperService", errorMessage);
                                        Toast.makeText(GIFWallpaperService.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                        // Draw the loaded GIF on the canvas
                                        resource.setBounds(0, 0, finalCanvas.getWidth(), finalCanvas.getHeight());
                                        resource.draw(finalCanvas);
                                        // Start the GIF animation
                                        resource.start();
                                        return true;
                                    }
                                })
                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    }
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }

                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, 20);
            }
        }


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                draw();
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
        }
    }
}
