package com.pilates.app.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pilates.app.R;
import com.pilates.app.controls.listeners.OnButtonClickListener;
import com.pilates.app.controls.listeners.OnRemoveByIdListener;

public class DocRow extends LinearLayout {

    private final RowType type;
    private Long id;
    private OnRemoveByIdListener listener;
    private RoundButton rbSelectType;
    private ImageView imgRemove;

    public enum RowType {
        DEFAULT, SIMPLE, TRAINER
    }
    public DocRow(Context context, RowType type, boolean selectMode, String selectText) {
        super(context);

        this.type = type;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(type == RowType.TRAINER ? R.layout.layout_doc_row_trainer : type == RowType.SIMPLE ? R.layout.layout_doc_row_simple : R.layout.layout_doc_row, this, true);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Bold.ttf");

        ((TextView)findViewById(R.id.tvTitle)).setTypeface(font);

        if(type == RowType.DEFAULT)
            ((TextView)findViewById(R.id.tvSubtitle)).setTypeface(fontBold);

        imgRemove = findViewById(R.id.imgRemove);
        imgRemove.setVisibility(selectMode ? GONE : VISIBLE);

        if(type == RowType.SIMPLE || type == RowType.TRAINER) {
            rbSelectType = findViewById(R.id.rbSelectType);
            rbSelectType.setText(selectText);
            rbSelectType.setVisibility(selectMode ? VISIBLE : GONE);
            rbSelectType.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.remove(id);
                }
            });
        }

        imgRemove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.remove(id);
            }
        });

        findViewById(R.id.pbLoader).setVisibility(View.GONE);
    }

    public void setRemoveListener(OnRemoveByIdListener listener){
        this.listener = listener;
    }

    DocRow getInstance() {
        return this;
    }

    public void setData(Long id, String title, String subtitle) {
        this.id = id;
        ((TextView)findViewById(R.id.tvTitle)).setText(title);

        if(type == RowType.DEFAULT)
            ((TextView)findViewById(R.id.tvSubtitle)).setText(subtitle);
    }

    public void setTrainerData(Long id, String name, String avatar) {
        this.id = id;
        name = name.length() > 20 ? name.substring(0, 20) + "..." : name;
        ((TextView)findViewById(R.id.tvTitle)).setText(name);
        Glide.with(getContext()).load(avatar).into((ImageView)findViewById(R.id.imgAvatar));
    }

    public void setLoading(boolean loading) {
        findViewById(R.id.pbLoader).setVisibility(loading ? View.VISIBLE : View.GONE);
        imgRemove.setVisibility(!loading ? View.VISIBLE : View.GONE);
    }
}
