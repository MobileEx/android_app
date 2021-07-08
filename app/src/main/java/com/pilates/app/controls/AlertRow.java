package com.pilates.app.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pilates.app.R;

import androidx.annotation.Nullable;

public class AlertRow extends FrameLayout {

    private String alertTitle;
    private String alertDescription;
    private TextView titleTextView;
    private TextView descriptionTextView;

    public AlertRow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.AlertRow, 0, 0);

        alertTitle = a.getString(R.styleable.AlertRow_alertTitle);
        alertDescription = a.getString(R.styleable.AlertRow_alertDescription);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_alert_row, this, true);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Bold.ttf");
        Typeface fontSemi = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");

        ((TextView)findViewById(R.id.tvTitle)).setTypeface(fontBold);
        ((TextView)findViewById(R.id.tvTitle)).setText(alertTitle);


        ((TextView)findViewById(R.id.tvSubtitle)).setTypeface(fontSemi);
        ((TextView)findViewById(R.id.tvSubtitle)).setText(alertDescription);
    }
}
