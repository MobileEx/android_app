package com.pilates.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;
import com.pilates.app.controls.ClassList;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.listeners.OnClassInfoActionListener;
import com.pilates.app.controls.listeners.OnClassListSelectedListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.controls.screens.ClassInfoPanel;
import com.pilates.app.model.dto.ClassListingInfoDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;

public class ClassListActivity extends AppCompatActivity {

    private ClassList clClasses;
    private ClassList clTrainers;
    private ClassInfoPanel classInfoPanel;
    private UserDataService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        userService = new UserDataService(this);
        clClasses = findViewById(R.id.clClasses);
        clTrainers = findViewById(R.id.clTrainers);

        clClasses.setVisibility(View.VISIBLE);
        clTrainers.setVisibility(View.GONE);

        classInfoPanel = findViewById(R.id.classInfoPanel);
        classInfoPanel.setCompleteListener(new OnClassInfoActionListener() {
            @Override
            public void main(String action) {
                clClasses.load();
                classInfoPanel.setVisibility(View.GONE);
            }

            @Override
            public void secondary() {
                classInfoPanel.setVisibility(View.GONE);
            }
        });
        clClasses.setClassSelectedListener(new OnClassListSelectedListener() {
            @Override
            public void selected(ClassListingInfoDto item) {
                classInfoPanel.setMode("new", false);
                classInfoPanel.setClassId(item.getId(), false);
                classInfoPanel.setVisibility(View.VISIBLE);
            }
        });
        classInfoPanel.setVisibility(View.GONE);

        ((RoundButtonGroup)findViewById(R.id.rbgSelectListType)).setSelectedValue("new");
        ((RoundButtonGroup)findViewById(R.id.rbgSelectListType)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                clClasses.load();
            }

            @Override
            public void removed(RoundButton button) {

            }
        });
        findViewById(R.id.imgIconBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });
    }


    @Override
    public void onBackPressed() {
        moveBack();
    }

    private void moveBack() {
        if(classInfoPanel.getVisibility() == View.VISIBLE)
            classInfoPanel.setVisibility(View.GONE);
        else
            finish();
    }
}
