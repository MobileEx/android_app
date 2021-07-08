package com.pilates.app.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pilates.app.R;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.model.dto.SelectionItemDto;
import com.pilates.app.model.dto.SelectionListDto;

import java.util.ArrayList;

public class RoundButtonGroup extends LinearLayout {
    private final boolean multiSelect;
    private OnRoundButtonClickListener buttonListener;

    public RoundButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundButtonGroup, 0, 0);
        multiSelect = a.getBoolean(R.styleable.RoundButtonGroup_multiSelect, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.round_button_group_layout, this, true);


    }

    void UpdateButtons(String val, boolean forceUpdate) {
        for(int i = 0; i < getChildCount(); i++) {
            if(!(getChildAt(i) instanceof RoundButton))
                continue;

            RoundButton btn = (RoundButton)getChildAt(i);
            if(!multiSelect || forceUpdate)
                btn.setActive(btn.getValue().equals(val));
            else if(btn.getValue().equals(val))
                btn.toggleActive();
        }
    }

    void UpdateButtonsMultiple(ArrayList<String> val, boolean forceUpdate) {
        for(int i = 0; i < getChildCount(); i++) {
            if(!(getChildAt(i) instanceof RoundButton))
                continue;

            RoundButton btn = (RoundButton)getChildAt(i);
            if(!multiSelect || forceUpdate)
                btn.setActive(val.contains(btn.getValue()));
            else if(val.contains(btn.getValue()))
                btn.toggleActive();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for(int i = 0; i < getChildCount(); i++) {
            if(!(getChildAt(i) instanceof RoundButton))
                continue;

            RoundButton btn = (RoundButton) getChildAt(i);
            btn.setMultiselect(multiSelect);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateButtons(btn.getValue(), false);
                    if(btn.isActive())
                        buttonListener.clicked(btn);
                    else
                        buttonListener.removed(btn);
                }
            });
        }


        UpdateButtons("", true);
    }

    public void setButtonListener(OnRoundButtonClickListener listener) {
        buttonListener = listener;
    }

    public void clearSelected() {
        UpdateButtons("", true);
    }

    public void setButtonSource(SelectionListDto list) {
        int idx = 0;
        removeAllViewsInLayout();
        for(SelectionItemDto item : list.getData()) {
            RoundButton rbItem = new RoundButton(getContext(), item.getId(), item.getName());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            rbItem.setMultiselect(multiSelect);
            rbItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateButtons(rbItem.getValue(), false);
                    if(rbItem.isActive())
                        buttonListener.clicked(rbItem);
                    else
                        buttonListener.removed(rbItem);
                }
            });
            params.rightMargin = getContext().getResources().getDimensionPixelSize(R.dimen.round_button_spacing);
            rbItem.setLayoutParams(params);
            addView(rbItem);
            idx++;
        }
    }

    public void setSelectedValue(String value) {
        UpdateButtons(value, true);
    }

    public void setSelectedValues(ArrayList<String> values) {
        UpdateButtonsMultiple(values, true);
    }
}
