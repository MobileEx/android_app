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

public class TimeBox extends FrameLayout {

    public TimeBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TimeBox, 0, 0);
        boolean smaller = a.getBoolean(R.styleable.TimeBox_tbSmaller, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(smaller ? R.layout.layout_time_box_smaller : R.layout.layout_time_box, this, true);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        ((TextView)findViewById(R.id.tvTime)).setTypeface(font);
        ((TextView)findViewById(R.id.tvTimeDuration)).setTypeface(font);
    }

    public void setTimeData(String time, int duration) {
        ((TextView)findViewById(R.id.tvTime)).setText(time);
        ((TextView)findViewById(R.id.tvTimeDuration)).setText(duration + "m");

        if(duration == 15) {
            setBackgroundResource(R.drawable.button_bg_cyan);
            findViewById(R.id.frameTimeAltPart).setBackgroundResource(R.drawable.button_bg_cyan_tb);
            ((TextView)findViewById(R.id.tvTime)).setTextColor(getContext()
                    .getResources().getColor(R.color.wooLabelCyan, null));
            ((TextView)findViewById(R.id.tvTimeDuration)).setTextColor(getContext()
                    .getResources().getColor(R.color.wooLabelCyan, null));
        } else if(duration == 30) {
            setBackgroundResource(R.drawable.button_bg_green);
            findViewById(R.id.frameTimeAltPart).setBackgroundResource(R.drawable.button_bg_green_tb);
            ((TextView)findViewById(R.id.tvTime)).setTextColor(getContext()
                    .getResources().getColor(R.color.wooLabelGreen, null));
            ((TextView)findViewById(R.id.tvTimeDuration)).setTextColor(getContext()
                    .getResources().getColor(R.color.wooLabelGreen, null));
        } else if(duration == 60) {
            setBackgroundResource(R.drawable.button_bg_error);
            findViewById(R.id.frameTimeAltPart).setBackgroundResource(R.drawable.button_bg_red_tb);
            ((TextView)findViewById(R.id.tvTime)).setTextColor(getContext()
                    .getResources().getColor(R.color.wooLabelRed, null));
            ((TextView)findViewById(R.id.tvTimeDuration)).setTextColor(getContext()
                    .getResources().getColor(R.color.wooLabelRed, null));
        } else {
            setBackgroundResource(R.drawable.button_bg_yellow);
            findViewById(R.id.frameTimeAltPart).setBackgroundResource(R.drawable.button_bg_yellow_tb);
            ((TextView)findViewById(R.id.tvTime)).setTextColor(getContext()
                    .getResources().getColor(R.color.labelYellow, null));
            ((TextView)findViewById(R.id.tvTimeDuration)).setTextColor(getContext()
                    .getResources().getColor(R.color.labelYellow, null));
        }
    }
}
