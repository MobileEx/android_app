package com.pilates.app.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pilates.app.R;

import androidx.annotation.Nullable;

public class HugeButton extends FrameLayout {

    private final int buttonStyle;
    private final int iconResource;
    private final boolean altVersion;
    private final int titleTextColor;
    private final int iconFrontResource;
    private final boolean slimVersion;
    private String hugeTitle;
    private String hugeDescription;
    private final int textColor;
    private ProgressBar loaderBar;
    private TextView titleTextView;
    private TextView descriptionTextView;

    public HugeButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.HugeButton, 0, 0);

        buttonStyle = a.getResourceId(R.styleable.HugeButton_hugeBG, 0);
        textColor = a.getColor(R.styleable.HugeButton_hugeTextColoring, Color.BLACK);
        hugeTitle = a.getString(R.styleable.HugeButton_hugeTitle);
        hugeDescription = a.getString(R.styleable.HugeButton_hugeDescription);
        iconResource = a.hasValue(R.styleable.HugeButton_hugeIcon) ?
                a.getResourceId(R.styleable.HugeButton_hugeIcon, 0) : 0;
        altVersion = a.getBoolean(R.styleable.HugeButton_hugeAlt, false);
        titleTextColor = a.getColor(R.styleable.HugeButton_hugeTitleColor, textColor);
        iconFrontResource = a.hasValue(R.styleable.HugeButton_hugeIconFront) ?
                a.getResourceId(R.styleable.HugeButton_hugeIconFront, 0) : 0;
        slimVersion = a.getBoolean(R.styleable.HugeButton_hugeSlim, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(slimVersion ? R.layout.layout_huge_button_slimmer : altVersion ? R.layout.layout_huge_button_alt : R.layout.layout_huge_button, this, true);

    }

    public void setLoading(boolean loading) {
        loaderBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        setClickable(!loading);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        titleTextView = findViewById(R.id.tvTitle);
        descriptionTextView = findViewById(R.id.tvDescription);
        loaderBar = (ProgressBar)findViewById(R.id.pbLoader);
        loaderBar.getIndeterminateDrawable().setColorFilter(textColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        loaderBar.setVisibility(View.GONE);

        setBackgroundResource(buttonStyle);
        titleTextView.setText(hugeTitle);
        descriptionTextView.setText(hugeDescription);
        titleTextView.setTextColor(titleTextColor);
        descriptionTextView.setTextColor(textColor);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Bold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");

        titleTextView.setTypeface(font);
        descriptionTextView.setTypeface(fontRegular);
        ((ImageView)findViewById(R.id.imgIcon)).setImageResource(iconResource);

        if(iconFrontResource != 0)
            ((ImageView)findViewById(R.id.imgIconFront)).setImageResource(iconFrontResource);
    }
}
