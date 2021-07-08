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

public class RoundButton extends FrameLayout {


    private final int buttonStyle;
    private final boolean hasIcon;
    private final int iconResource;
    private String specSetup;
    private boolean classOptionMode;
    private String buttonText;
    private final int textColor;
    private final boolean textIsBold;
    private ProgressBar loaderBar;
    private TextView buttonTextView;

    private String value;
    private boolean active;
    private ImageView imgChecked;
    private boolean isLoading;

    public RoundButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundButton, 0, 0);

        buttonStyle = a.getResourceId(R.styleable.RoundButton_buttonBg, 0);
        buttonText = a.getString(R.styleable.RoundButton_textLabel);
        textColor = a.getColor(R.styleable.RoundButton_textColoring, Color.BLACK);
        textIsBold = a.getBoolean(R.styleable.RoundButton_textIsBold, true);
        value = a.getString(R.styleable.RoundButton_selectionValue);
        hasIcon = a.getBoolean(R.styleable.RoundButton_hasIcon, false);
        iconResource = a.hasValue(R.styleable.RoundButton_buttonIcon) ?
                a.getResourceId(R.styleable.RoundButton_buttonIcon, 0) : 0;
        specSetup = a.getString(R.styleable.RoundButton_specificSetup);
        if(specSetup == null)
            specSetup = "default";

        boolean tiny = a.getBoolean(R.styleable.RoundButton_buttonTiny, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(tiny ? R.layout.layout_round_button_tiny
                : specSetup.equals("dashboard_green") ? R.layout.layout_round_button_dashboard_green
                : specSetup.equals("dashboard_white") ? R.layout.layout_round_button_dashboard_white
                : specSetup.equals("dashboard_grey") ? R.layout.layout_round_button_dashboard_grey
                : specSetup.equals("icon_slimmer") ? R.layout.layout_round_button_icon_slimmer
                : specSetup.equals("button_underscore") ? R.layout.layout_round_button_underscore
                : hasIcon ? R.layout.layout_round_button_icon
                : R.layout.layout_round_button, this, true);

    }

    public RoundButton(Context context, String value, String text) {
        super(context);

        buttonText = text;
        this.value = value;

        classOptionMode = true;
        buttonStyle = R.drawable.button_bg_gray;
        textColor = getResources().getColor(R.color.labelDark, null);
        textIsBold = true;
        hasIcon = false;
        iconResource = 0;
        specSetup = "default";
        boolean tiny = false;
        setClickable(true);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_round_button_add_class_option, this, true);

        buttonTextView = ((TextView)findViewById(R.id.tvButtonText));
        loaderBar = (ProgressBar)findViewById(R.id.pbLoader);
        loaderBar.getIndeterminateDrawable().setColorFilter(textColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        loaderBar.setVisibility(View.GONE);

        findViewById(R.id.frameButton).setBackgroundResource(buttonStyle);
        buttonTextView.setText(buttonText);
        buttonTextView.setTextColor(textColor);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), textIsBold ? "fonts/Poppins-Bold.ttf" : "fonts/Poppins-Regular.ttf");
        buttonTextView.setTypeface(font);

        imgChecked = findViewById(R.id.imgChecked);
        imgChecked.setVisibility(View.GONE);

    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        loaderBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonTextView.setVisibility(!loading ? View.VISIBLE : View.GONE);

        setClickable(!loading);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        buttonTextView = ((TextView)findViewById(R.id.tvButtonText));
        loaderBar = (ProgressBar)findViewById(R.id.pbLoader);
        loaderBar.getIndeterminateDrawable().setColorFilter(textColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        loaderBar.setVisibility(View.GONE);

        if(!specSetup.equals("button_underscore"))
            findViewById(R.id.frameButton).setBackgroundResource(buttonStyle);
        else
            findViewById(R.id.frameUnderscore).setBackgroundResource(buttonStyle);

        buttonTextView.setText(buttonText);
        buttonTextView.setTextColor(textColor);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), textIsBold ? "fonts/Poppins-Bold.ttf" : "fonts/Poppins-Regular.ttf");
        buttonTextView.setTypeface(font);

        imgChecked = findViewById(R.id.imgChecked);
        imgChecked.setVisibility(hasIcon ? View.VISIBLE : View.GONE);

        if(hasIcon)
            imgChecked.setImageResource(iconResource);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setActive(boolean active) {
        this.active = active;

        if(!specSetup.equals("button_underscore")) {
            findViewById(R.id.frameButton).setBackgroundResource(active ? R.drawable.button_bg_purple : R.drawable.button_bg_gray);
            buttonTextView.setTextColor(active ? Color.WHITE : getResources().getColor(R.color.labelDark, null));

            imgChecked.setImageResource(active ? R.drawable.icon_checked : R.drawable.icon_unchecked);
        }
        else {
            findViewById(R.id.frameUnderscore).setBackgroundResource(active ? R.drawable.button_bg_purple : R.drawable.button_bg_gray);
            buttonTextView.setTextColor(active ? getResources().getColor(R.color.colorPrimaryDark, null) : getResources().getColor(R.color.labelFaded, null));
        }
    }

    public void setMultiselect(boolean multiSelect) {
        imgChecked.setVisibility(!classOptionMode && multiSelect ? View.VISIBLE : View.GONE);
    }

    public void toggleActive() {
        active = !active;
        setActive(active);
    }

    public boolean isActive() {
        return active;
    }

    public void setText(String text) {
        buttonText = text;
        buttonTextView.setText(text);
    }

    public void setButtonBackground(int resource) {
        findViewById(R.id.frameButton).setBackgroundResource(resource);
    }

    public void setButtonTextColor(int color) {
        buttonTextView.setTextColor(color);
    }

    public void setIconResource(int resource) {
        imgChecked.setImageResource(resource);
    }
}
