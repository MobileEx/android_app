package com.pilates.app.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.pilates.app.R;

import androidx.annotation.Nullable;

public class GalleryUploader extends LinearLayout {
    private final ImageView imageView;
    private OnClickListener removeListener;
    private Target currentLoad;

    public GalleryUploader(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_gallery_uploader, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IconButton, 0, 0);
        a.recycle();

        imageView = findViewById(R.id.image);
        findViewById(R.id.imgRemove).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener.onClick(v);
            }
        });
    }

    GalleryUploader getInstance() {
        return this;
    }

    public void setLoading(boolean loading) {
        findViewById(R.id.image).setVisibility(loading ? View.GONE : View.VISIBLE);
        findViewById(R.id.pbLoader).setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    public void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void setImageResource(int id) {
        imageView.setImageResource(id);
    }

    public ImageView exposeImage() {
        return imageView;
    }

    public void setRemoveListener(OnClickListener listener) {
        removeListener = listener;
    }

    public void loadImage(String url) {
        if(currentLoad != null) {
            Glide.with(getContext()).clear(currentLoad);
            currentLoad = null;
        }
        setLoading(true);
        currentLoad = Glide.with(getContext()).load(url + "?rnd=" + Math.random())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        setLoading(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setLoading(false);
                        return false;
                    }
                })
                .into(imageView);
    }

    public void setShowcaseMode() {
        findViewById(R.id.imgRemove).setVisibility(GONE);
    }
}
