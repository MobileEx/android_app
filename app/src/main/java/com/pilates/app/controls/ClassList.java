package com.pilates.app.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pilates.app.R;
import com.pilates.app.controls.listeners.OnClassListSelectedListener;
import com.pilates.app.controls.listeners.OnOperationCompleteWithIdListener;
import com.pilates.app.model.dto.ClassFilterRequest;
import com.pilates.app.model.dto.ClassFilteredListDto;
import com.pilates.app.model.dto.ClassListingInfoDto;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;

import androidx.annotation.Nullable;

public class ClassList extends FrameLayout {

    private final ProgressBar pbClassLoader;
    private final LinearLayout layoutClassItems;
    private final String mode;
    private UserDataService userService;
    private int page;
    private ClassFilteredListDto fetchedData;
    private OnClassListSelectedListener listener;

    public ClassList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ClassList, 0, 0);
        mode = a.getString(R.styleable.ClassList_clMode);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_class_list, this, true);

        pbClassLoader = findViewById(R.id.pbClassListLoader);
        pbClassLoader.setVisibility(GONE);

        layoutClassItems = findViewById(R.id.layoutClassItems);
        userService = new UserDataService(getContext());
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility == View.VISIBLE)
            load();
    }

    public void load() {
        pbClassLoader.setVisibility(VISIBLE);
        layoutClassItems.removeAllViewsInLayout();

        if(mode.equals("setups"))
            userService.getTrainerClassSetupsList(page, new OnRequestResult<ClassFilteredListDto>() {
                @Override
                public void requestComplete(ClassFilteredListDto data) {
                    fetchedData = data;
                    displayData();
                    pbClassLoader.setVisibility(GONE);
                }

                @Override
                public void requestError(VolleyError ex) {
                    pbClassLoader.setVisibility(GONE);
                }
            });
        else if(mode.equals("own")) {
            ClassFilterRequest request = new ClassFilterRequest();
            request.setMode("own");
            request.setPage(0);

            userService.getClassList(request, new OnRequestResult<ClassFilteredListDto>() {
                @Override
                public void requestComplete(ClassFilteredListDto data) {
                    fetchedData = data;
                    displayData();
                    pbClassLoader.setVisibility(GONE);
                }

                @Override
                public void requestError(VolleyError ex) {
                    pbClassLoader.setVisibility(GONE);
                }
            });
        }
        else if(mode.equals("history")) {
            ClassFilterRequest request = new ClassFilterRequest();
            request.setMode("history");
            request.setPage(0);

            userService.getClassList(request, new OnRequestResult<ClassFilteredListDto>() {
                @Override
                public void requestComplete(ClassFilteredListDto data) {
                    fetchedData = data;
                    displayData();
                    pbClassLoader.setVisibility(GONE);
                }

                @Override
                public void requestError(VolleyError ex) {
                    pbClassLoader.setVisibility(GONE);
                }
            });
        }
        else if(mode.equals("new")) {
            ClassFilterRequest request = new ClassFilterRequest();
            request.setMode("new");
            request.setPage(0);

            userService.getClassList(request, new OnRequestResult<ClassFilteredListDto>() {
                @Override
                public void requestComplete(ClassFilteredListDto data) {
                    fetchedData = data;
                    displayData();
                    pbClassLoader.setVisibility(GONE);
                }

                @Override
                public void requestError(VolleyError ex) {
                    pbClassLoader.setVisibility(GONE);
                }
            });
        }
    }

    private void displayData() {
        layoutClassItems.removeAllViewsInLayout();

        for(ClassListingInfoDto item : fetchedData.getData()) {
            ClassRow row = new ClassRow(getContext(), item);
            LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = getContext().getResources().getDimensionPixelSize(R.dimen.round_button_spacing);
            params.leftMargin = getContext().getResources().getDimensionPixelSize(R.dimen.class_row_side_spacing);
            params.rightMargin = getContext().getResources().getDimensionPixelSize(R.dimen.class_row_side_spacing);
            layoutClassItems.addView(row);
            row.setLayoutParams(params);

            row.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.selected(item);
                }
            });
        }
    }

    public void setClassSelectedListener(OnClassListSelectedListener listener) {
        this.listener = listener;
    }
}
