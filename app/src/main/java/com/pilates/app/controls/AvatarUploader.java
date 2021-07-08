package com.pilates.app.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pilates.app.R;
import com.pilates.app.controls.listeners.OnButtonClickListener;

public class AvatarUploader extends LinearLayout {
    private final ImageView imageView;

    public AvatarUploader(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_avatar_uploader, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IconButton, 0, 0);
        a.recycle();

        imageView = findViewById(R.id.imgUploadAvatar);
    }

    AvatarUploader getInstance() {
        return this;
    }

    public void setLoading(boolean loading) {
        findViewById(R.id.cvCardView).setVisibility(loading ? View.GONE : View.VISIBLE);
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
}
