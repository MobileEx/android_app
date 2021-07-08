package com.pilates.app.controls.simple;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

public class WizardStepTxtImage {
    private View parentView;
    private TextView text;
    private ImageView image;
    private int resourceActive;
    private int resourceInactive;
    private int resourceComplete;
    private int activeTextColor;
    private int defaultTextColor;

    public WizardStepTxtImage(Context context,  TextView text, ImageView image,
                              int resourceActive, int resourceInactive, int resourceComplete,
                              int activeTextColor, int defaultTextColor) {
        this.text = text;
        this.image = image;
        this.resourceActive = resourceActive;
        this.resourceInactive = resourceInactive;
        this.resourceComplete = resourceComplete;
        this.activeTextColor = activeTextColor;
        this.defaultTextColor = defaultTextColor;

        parentView = (View)text.getParent();
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-SemiBold.ttf");
        text.setTypeface(font);
    }

    public void setActive(boolean active, boolean complete) {
        text.setTextColor(active || complete ? activeTextColor : defaultTextColor);
        image.setImageResource(complete ? resourceComplete : active ? resourceActive : resourceInactive);
    }

    public int getResourceActive() {
        return resourceActive;
    }

    public void setResourceActive(int resourceActive) {
        this.resourceActive = resourceActive;
    }

    public void setResourceInactive(int resourceInactive) {
        this.resourceInactive = resourceInactive;
    }

    public void setResourceComplete(int resourceComplete) {
        this.resourceComplete = resourceComplete;
    }

    public void setActiveTextColor(int activeTextColor) {
        this.activeTextColor = activeTextColor;
    }

    public void setDefaultTextColor(int defaultTextColor) {
        this.defaultTextColor = defaultTextColor;
        text.setTextColor(defaultTextColor);
    }

    public View getView() {
        return parentView;
    }
}
