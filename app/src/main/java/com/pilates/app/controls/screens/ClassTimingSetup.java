package com.pilates.app.controls.screens;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.pilates.app.R;
import com.pilates.app.controls.DateTimeSelector;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.listeners.OnDateTimePickedListener;
import com.pilates.app.controls.listeners.OnOperationCompleteWithIdListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.model.dto.ClassTimingDto;
import com.pilates.app.model.dto.SelectionItemDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.enums.ExperienceLevel;
import com.pilates.app.model.dto.enums.StreamingType;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class ClassTimingSetup extends FrameLayout {

    private final ProgressBar pbLoader;
    private final TextView tvError;
    private UserDataService userService;
    private OnOperationCompleteWithIdListener completeListener;
    private ClassTimingDto specData;
    private Long classId = (long)0;
    private DateTimeSelector dts;

    public ClassTimingSetup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_class_timing, this, true);

        userService = new UserDataService(context);

        pbLoader = findViewById(R.id.pbTimingLoader);
        pbLoader.setVisibility(GONE);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
        ((TextView)findViewById(R.id.tvClassTiming)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassStartDate)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassStartTime)).setTypeface(font);
        ((TextView)findViewById(R.id.tvMultipleClasses)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassNumberOfClasses)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassesInfo)).setTypeface(font);

        tvError =((TextView)findViewById(R.id.tvError));
        tvError.setTypeface(font);
        tvError.setVisibility(GONE);

        ((EditText)findViewById(R.id.classStartDate)).setKeyListener(null);
        ((EditText)findViewById(R.id.classStartDate)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dts.open("class_date", false);
            }
        });

        RoundButton rbContinue = findViewById(R.id.rbClassTimingContinue);
        rbContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText)findViewById(R.id.classStartDate)).getText() != null
                        && ((EditText)findViewById(R.id.classStartDate)).getText().toString().length() > 0)
                    specData.setStartDate(((EditText)findViewById(R.id.classStartDate)).getText().toString());
                if(((EditText)findViewById(R.id.classNumberOfClasses)).getText() != null
                        && ((EditText)findViewById(R.id.classNumberOfClasses)).getText().toString().length() > 0)
                    specData.setNumberOfClasses(Integer.parseInt(((EditText)findViewById(R.id.classNumberOfClasses)).getText().toString()));

                tvError.setVisibility(GONE);
                rbContinue.setLoading(true);
                userService.saveClassTiming(specData, new OnRequestResult<StatusMessageDto>() {
                    @Override
                    public void requestComplete(StatusMessageDto data) {
                        rbContinue.setLoading(false);
                        if(data.getError()) {
                            tvError.setText(data.getMessage());
                            tvError.setVisibility(VISIBLE);
                        }
                        else
                            completeListener.complete(Long.parseLong(data.getMessage()));
                    }

                    @Override
                    public void requestError(VolleyError ex) {
                        rbContinue.setLoading(false);
                        tvError.setText("Couldn't save timing.");
                        tvError.setVisibility(VISIBLE);
                    }
                });
            }
        });

        ((EditText)findViewById(R.id.classStartDate)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayInfoText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((EditText)findViewById(R.id.classNumberOfClasses)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() > 0)
                    specData.setNumberOfClasses(Integer.parseInt(s.toString()));

                displayInfoText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.classNumberOfClasses).setVisibility(GONE);
        findViewById(R.id.tvClassNumberOfClasses).setVisibility(GONE);
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

        userService.getClassTiming(classId, new OnRequestResult<ClassTimingDto>() {
            @Override
            public void requestComplete(ClassTimingDto data) {
                pbLoader.setVisibility(GONE);
                findViewById(R.id.layoutFields).setVisibility(VISIBLE);
                specData = data;

                setupClassTiming();
            }

            @Override
            public void requestError(VolleyError ex) {
                pbLoader.setVisibility(GONE);
                findViewById(R.id.layoutFields).setVisibility(VISIBLE);
            }
        });
    }

    private void setupClassTiming() {
        ((RoundButtonGroup)findViewById(R.id.rbgSelectMultipleClasses)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                specData.setMultipleClasses(Boolean.parseBoolean(button.getValue()));

                findViewById(R.id.classNumberOfClasses).setVisibility(specData.getMultipleClasses() ? VISIBLE : GONE);
                findViewById(R.id.tvClassNumberOfClasses).setVisibility(specData.getMultipleClasses() ? VISIBLE : GONE);
                displayInfoText();
            }

            @Override
            public void removed(RoundButton button) {

            }
        });

        Spinner spnTiming = findViewById(R.id.spnClassStartTime);
        ArrayList<String> startTime = new ArrayList<>();
        for (int i = 0; i < 96; i++) {
            int minute = i * 15;
            int hour = minute / 60;
            int minuteNormalized = minute - (hour * 60);
            startTime.add(String.format("%02d", hour) + ":" + String.format("%02d", minuteNormalized));
        }
        ArrayAdapter<String> adapterDurations = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, startTime);
        adapterDurations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTiming.setAdapter(adapterDurations);
        spnTiming.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specData.setStartTime(startTime.get(position));
                displayInfoText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnTiming.setSelection(specData.getStartTime() != null ? startTime.indexOf(specData.getStartTime()) : 0);
        specData.setStartTime(startTime.get(spnTiming.getSelectedItemPosition()));
        ((EditText)findViewById(R.id.classStartDate)).setText(specData.getStartDate());
        if(specData.getMultipleClasses() != null) {
            ((RoundButtonGroup) findViewById(R.id.rbgSelectMultipleClasses)).setSelectedValue(specData.getMultipleClasses().toString());
            findViewById(R.id.classNumberOfClasses).setVisibility(specData.getMultipleClasses() ? VISIBLE : GONE);
        }
        ((EditText)findViewById(R.id.classNumberOfClasses)).setText(specData.getNumberOfClasses() != null ? specData.getNumberOfClasses().toString() : null);

        displayInfoText();
    }

    private void displayInfoText() {
        if(specData.getStartDate() == null || specData.getStartTime() == null || specData.getMultipleClasses() == null) {
            ((TextView)findViewById(R.id.tvClassesInfo)).setVisibility(GONE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("MMM dd");
        LocalDateTime dateTime = LocalDateTime.parse(specData.getStartDate() + " " + specData.getStartTime(), formatter);

        ((TextView)findViewById(R.id.tvClassesInfo)).setVisibility(VISIBLE);
        if(!specData.getMultipleClasses())
            ((TextView)findViewById(R.id.tvClassesInfo))
                    .setText("Your class will start on " + dateTime.format(targetFormat) + " at " + specData.getStartTime() + ".");
        else if(specData.getNumberOfClasses() != null && specData.getNumberOfClasses() > 0) {
            ((TextView)findViewById(R.id.tvClassesInfo))
                    .setText("Your program will have " + specData.getNumberOfClasses()
                            + " weekly classes, starting at " + specData.getStartTime() + ", with first class on "
                            + dateTime.format(targetFormat) + " and last class on "
                            + dateTime.plusWeeks(specData.getNumberOfClasses()).format(targetFormat) + ".");
        }
        else
            ((TextView)findViewById(R.id.tvClassesInfo)).setVisibility(GONE);
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

    public void init(DateTimeSelector dts) {
        this.dts = dts;
        dts.addListener("class_date", new OnDateTimePickedListener() {
            @Override
            public void pickedDate(int year, int month, int day, String formatted) {
                specData.setStartDate(formatted);
                ((EditText)findViewById(R.id.classStartDate)).setText(formatted);
            }

            @Override
            public void pickedTime(int hour, int minutes, String formatted) {

            }
        });
    }
}
