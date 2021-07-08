package com.pilates.app.controls;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pilates.app.R;
import com.pilates.app.controls.listeners.OnButtonClickListener;
import com.pilates.app.controls.listeners.OnSlidingPanelEventListener;

import androidx.annotation.NonNull;

public class SlidingPanel extends FrameLayout {
    private final boolean helpMode;
    private OnSlidingPanelEventListener listener;
    private float mod = 0.2f;

    public SlidingPanel(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SlidingPanel, 0, 0);

        helpMode = a.getBoolean(R.styleable.SlidingPanel_helpMode, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(helpMode ? R.layout.layout_help_panel : R.layout.layout_slide_panel, this, true);


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        getThisView().setY(height * (1f + mod));

        findViewById(R.id.btnClosePanel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getThisView().animate().y(height * (1f + mod));
            }
        });

        if(!helpMode) {
            ((IconButtonGroup) findViewById(R.id.ibgLayout)).setButtonListener(new OnButtonClickListener() {
                @Override
                public void clicked(IconButton button) {
                    listener.changeLayout(button.getDataTag());
                }
            });
        }
        else {

            Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Bold.ttf");
            Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
            ((TextView)findViewById(R.id.tvTitle)).setTypeface(fontBold);

            if(helpMode)
                ((TextView)findViewById(R.id.tvText)).setTypeface(fontRegular);
        }
    }

    private FrameLayout getThisView() {
        return this;
    }

    public void setListener(OnSlidingPanelEventListener listener) {
        this.listener = listener;
    }

    public void setHeightMod(float mod) {
        this.mod = mod;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        getThisView().setY(height * (1f + mod));

        findViewById(R.id.btnClosePanel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getThisView().animate().y(height * (1f + mod));
            }
        });

        if(!helpMode) {
            ((IconButtonGroup) findViewById(R.id.ibgLayout)).setButtonListener(new OnButtonClickListener() {
                @Override
                public void clicked(IconButton button) {
                    listener.changeLayout(button.getDataTag());
                }
            });
        }
    }

    public void showPanel() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        getParent().requestTransparentRegion(this);

        //FrameLayout fds = findViewById(R.id.frameDisplaySettings); THIS WAS WORKING
        FrameLayout fds = this;
        fds.animate().y(height * mod).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getParent().requestTransparentRegion(getThisView());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setHelpText(String text) {
        ((TextView)findViewById(R.id.tvText)).setText(text);
    }
}
