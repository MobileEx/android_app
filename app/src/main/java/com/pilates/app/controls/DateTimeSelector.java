package com.pilates.app.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pilates.app.R;
import com.pilates.app.controls.listeners.OnDateTimePickedListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;

public class DateTimeSelector extends FrameLayout {

    private HashMap<String, OnDateTimePickedListener> listeners;
    private String activeSessionKey;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    public DateTimeSelector(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_date_time_picker, this, true);

        listeners = new HashMap<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");

        ((TextView)findViewById(R.id.tvPickTitle)).setTypeface(font);
        ((TextView)findViewById(R.id.tvDay)).setTypeface(font);
        ((TextView)findViewById(R.id.tvMonth)).setTypeface(font);
        ((TextView)findViewById(R.id.tvYear)).setTypeface(font);

        // setup years
        Spinner spnYears = findViewById(R.id.spnYear);
        ArrayList<String> years = new ArrayList<>();
        for (int i = LocalDateTime.now().getYear(); i >= 1990; i--) {
            years.add(i + "");
        }
        ArrayAdapter<String> adapterYears = new ArrayAdapter<String>(getContext(),
                R.layout.layout_spinner_dropdown_item, years);
        adapterYears.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spnYears.setAdapter(adapterYears);
        spnYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt(years.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnYears.setSelection(0);

        // setup months
        Spinner spnMonths = findViewById(R.id.spnMonth);
        ArrayList<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i + "");
        }
        ArrayAdapter<String> adapterMonths = new ArrayAdapter<String>(getContext(),
                R.layout.layout_spinner_dropdown_item, months);
        adapterMonths.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spnMonths.setAdapter(adapterMonths);
        spnMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = Integer.parseInt(months.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnMonths.setSelection(months.indexOf(LocalDateTime.now().getMonthValue() + ""));

        // setup days
        Spinner spnDays = findViewById(R.id.spnDay);
        ArrayList<String> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            days.add(i + "");
        }
        ArrayAdapter<String> adapterDays = new ArrayAdapter<String>(getContext(),
                R.layout.layout_spinner_dropdown_item, days);
        adapterDays.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spnDays.setAdapter(adapterDays);
        spnDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = Integer.parseInt(days.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnDays.setSelection(days.indexOf(LocalDateTime.now().getDayOfMonth() + ""));

        ((RoundButton)findViewById(R.id.rbPick)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listeners.get(activeSessionKey).pickedDate(selectedYear, selectedMonth, selectedDay,
                        String.format("%02d", selectedDay) + "/" +
                                String.format("%02d", selectedMonth) + "/" +
                                selectedYear);
                setVisibility(GONE);
            }
        });

        findViewById(R.id.layoutDate).setVisibility(VISIBLE);
        findViewById(R.id.layoutTime).setVisibility(GONE);
    }

    public void addListener(String key, OnDateTimePickedListener listener) {
        listeners.put(key, listener);
    }

    public void open(String key, boolean timeMode) {
        activeSessionKey = key;
        setVisibility(VISIBLE);

        findViewById(R.id.layoutDate).setVisibility(timeMode ? GONE : VISIBLE);
        findViewById(R.id.layoutTime).setVisibility(timeMode ? VISIBLE : GONE);
    }
}
