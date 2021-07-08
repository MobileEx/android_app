package com.pilates.app.controls.screens;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.pilates.app.R;
import com.pilates.app.controls.DocRow;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.listeners.OnOperationCompleteListener;
import com.pilates.app.controls.listeners.OnOperationCompleteWithIdListener;
import com.pilates.app.controls.listeners.OnRemoveByIdListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.model.dto.ClassSpecDto;
import com.pilates.app.model.dto.SelectionItemDto;
import com.pilates.app.model.dto.SelectionListDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.enums.ExperienceLevel;
import com.pilates.app.model.dto.enums.StreamingType;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class ClassSpecificationSetup extends FrameLayout {

    private final ProgressBar pbLoader;
    private final TextView tvError;
    private UserDataService userService;
    private OnOperationCompleteWithIdListener completeListener;
    private ClassSpecDto specData;
    private Long classId = (long)0;

    public ClassSpecificationSetup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_class_spec, this, true);

        userService = new UserDataService(context);

        pbLoader = findViewById(R.id.pbSpecLoader);
        pbLoader.setVisibility(GONE);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
        ((TextView)findViewById(R.id.tvClassSpec)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassName)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassDuration)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassExpLevel)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassStreamingType)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassPrice)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassMaxParticipants)).setTypeface(font);

        tvError =((TextView)findViewById(R.id.tvError));
        tvError.setTypeface(font);
        tvError.setVisibility(GONE);

        RoundButton rbContinue = findViewById(R.id.rbClassSpecContinue);
        rbContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText)findViewById(R.id.className)).getText() != null &&
                        ((EditText)findViewById(R.id.className)).getText().length() > 0)
                    specData.setClassName(((EditText)findViewById(R.id.className)).getText().toString());
                if(((EditText)findViewById(R.id.classPrice)).getText() != null
                    &&((EditText)findViewById(R.id.classPrice)).getText().toString().length() > 0)
                    specData.setPrice(Double.parseDouble(((EditText)findViewById(R.id.classPrice)).getText().toString()));
                if(((EditText)findViewById(R.id.classMaxParticipants)).getText() != null
                        &&((EditText)findViewById(R.id.classMaxParticipants)).getText().toString().length() > 0)
                    specData.setMaxUsers(Integer.parseInt(((EditText)findViewById(R.id.classMaxParticipants)).getText().toString()));

                tvError.setVisibility(GONE);
                rbContinue.setLoading(true);
                userService.saveClassSpec(specData, new OnRequestResult<StatusMessageDto>() {
                    @Override
                    public void requestComplete(StatusMessageDto data) {
                        rbContinue.setLoading(false);
                        if(data.getError()) {
                            tvError.setText(data.getMessage());
                            tvError.setVisibility(VISIBLE);
                        }
                        else {
                            classId = Long.parseLong(data.getMessage());
                            completeListener.complete(Long.parseLong(data.getMessage()));
                        }
                    }

                    @Override
                    public void requestError(VolleyError ex) {
                        rbContinue.setLoading(false);
                        tvError.setText("Couldn't save specification.");
                        tvError.setVisibility(VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility == View.VISIBLE)
           load();
    }


    private void load() {
        pbLoader.setVisibility(VISIBLE);
        findViewById(R.id.layoutFields).setVisibility(GONE);

        userService.getClassSpecification(classId, new OnRequestResult<ClassSpecDto>() {
            @Override
            public void requestComplete(ClassSpecDto data) {
                pbLoader.setVisibility(GONE);
                findViewById(R.id.layoutFields).setVisibility(VISIBLE);
                specData = data;
                classId = data.getId();

                setupClassSpec();
            }

            @Override
            public void requestError(VolleyError ex) {
                pbLoader.setVisibility(GONE);
                findViewById(R.id.layoutFields).setVisibility(VISIBLE);
            }
        });
    }

    private void setupClassSpec() {
        ((RoundButtonGroup)findViewById(R.id.rbgSelectExperienceLevel)).setButtonSource(specData.getExpLevels());
        ((RoundButtonGroup)findViewById(R.id.rbgSelectExperienceLevel)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                specData.setExperienceLevel(ExperienceLevel.valueOf(button.getValue()));
            }

            @Override
            public void removed(RoundButton button) {

            }
        });
        ((RoundButtonGroup)findViewById(R.id.rbgSelectStreamingType)).setButtonSource(specData.getStreamingTypes());
        ((RoundButtonGroup)findViewById(R.id.rbgSelectStreamingType)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                specData.setStreamingType(StreamingType.valueOf(button.getValue()));
            }

            @Override
            public void removed(RoundButton button) {

            }
        });

        Spinner spnDurations = findViewById(R.id.spnClassDuration);
        ArrayList<String> durations = new ArrayList<>();
        ArrayList<String> durationIds = new ArrayList<>();
        for (SelectionItemDto item : specData.getClassDurations().getData()) {
            durations.add(item.getName());
            durationIds.add(item.getId());
        }
        ArrayAdapter<String> adapterDurations = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, durations);
        adapterDurations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDurations.setAdapter(adapterDurations);
        spnDurations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specData.setDuration(Integer.parseInt(durationIds.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(specData.getExperienceLevel() != null)
            ((RoundButtonGroup)findViewById(R.id.rbgSelectExperienceLevel)).setSelectedValue(specData.getExperienceLevel().toString());

        if(specData.getStreamingType() != null)
            ((RoundButtonGroup)findViewById(R.id.rbgSelectStreamingType)).setSelectedValue(specData.getStreamingType().toString());

        spnDurations.setSelection(durationIds.indexOf(specData.getDuration() + ""));
        ((EditText)findViewById(R.id.className)).setText(specData.getClassName());
        ((EditText)findViewById(R.id.classPrice)).setText(specData.getPrice() != null ? specData.getPrice().toString() : null);
        ((EditText)findViewById(R.id.classMaxParticipants)).setText(specData.getMaxUsers() != null ? specData.getMaxUsers().toString() : null);

    }

    public void setCompleteListener(OnOperationCompleteWithIdListener completeListener) {
        this.completeListener = completeListener;
    }

    public void setClassId(Long id) {
        classId = id;
    }

    public Long getClassId() {
        return classId;
    }
}
