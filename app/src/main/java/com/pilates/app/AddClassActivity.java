package com.pilates.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.pilates.app.controls.ClassList;
import com.pilates.app.controls.IconButton;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.listeners.OnButtonClickListener;
import com.pilates.app.controls.listeners.OnClassInfoActionListener;
import com.pilates.app.controls.listeners.OnClassListSelectedListener;
import com.pilates.app.controls.listeners.OnOperationCompleteWithIdListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.controls.screens.ClassDetailsSetup;
import com.pilates.app.controls.screens.ClassInfoPanel;
import com.pilates.app.controls.screens.ClassSpecificationSetup;
import com.pilates.app.controls.screens.ClassTimingSetup;
import com.pilates.app.controls.screens.GallerySetup;
import com.pilates.app.controls.simple.WizardStepTxtImage;
import com.pilates.app.model.dto.AddClassSelectionsDto;
import com.pilates.app.model.dto.ClassListingInfoDto;
import com.pilates.app.model.dto.SelectionItemDto;
import com.pilates.app.model.dto.SelectionListDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.util.Constant;

import java.util.ArrayList;

public class AddClassActivity extends AppCompatActivity {

    private ArrayList<WizardStepTxtImage> wizardSteps;
    private UserDataService userService;
    private int mainScreen;
    private int currentStep;
    private ClassSpecificationSetup classSpec;
    private ClassTimingSetup classTiming;
    private ClassDetailsSetup classDetails;
    private GallerySetup gallerySetup;
    private ClassInfoPanel classInfoPanel;
    private ClassList clDraft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        userService = new UserDataService(this);
        wizardSteps = new ArrayList<>();
        wizardSteps.add(new WizardStepTxtImage(this, findViewById(R.id.tvStep1), findViewById(R.id.imgStep1),
                R.drawable.icon_step_active,
                R.drawable.icon_step_inactive,
                R.drawable.icon_step_complete,
                getResources().getColor(R.color.wooGreen, null),
                getResources().getColor(R.color.wooWeirdPurple, null)));
        wizardSteps.get(0).getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveStep(0);
            }
        });
        wizardSteps.add(new WizardStepTxtImage(this, findViewById(R.id.tvStep2), findViewById(R.id.imgStep2),
                R.drawable.icon_step_active,
                R.drawable.icon_step_inactive,
                R.drawable.icon_step_complete,
                getResources().getColor(R.color.wooGreen, null),
                getResources().getColor(R.color.wooWeirdPurple, null)));
        wizardSteps.get(1).getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentStep >= 1)
                    setActiveStep(1);
            }
        });
        wizardSteps.add(new WizardStepTxtImage(this, findViewById(R.id.tvStep3), findViewById(R.id.imgStep3),
                R.drawable.icon_step_active,
                R.drawable.icon_step_inactive,
                R.drawable.icon_step_complete,
                getResources().getColor(R.color.wooGreen, null),
                getResources().getColor(R.color.wooWeirdPurple, null)));
        wizardSteps.get(2).getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentStep >= 2)
                    setActiveStep(2);
            }
        });

        findViewById(R.id.frameCloningClass).setVisibility(View.GONE);

        clDraft = findViewById(R.id.clDraft);
        clDraft.setClassSelectedListener(new OnClassListSelectedListener() {
            @Override
            public void selected(ClassListingInfoDto item) {
                findViewById(R.id.frameCloningClass).setVisibility(View.VISIBLE);

                userService.cloneClass(item.getId(), new OnRequestResult<StatusMessageDto>() {
                    @Override
                    public void requestComplete(StatusMessageDto data) {
                        if(!data.getError()) {
                            classSpec.setClassId(Long.parseLong(data.getMessage()));
                            mainScreen = 1;
                            findViewById(R.id.frameCopyClass).setVisibility(View.GONE);
                            findViewById(R.id.frameForm).setVisibility(View.VISIBLE);
                            setActiveStep(0);
                        }
                        findViewById(R.id.frameCloningClass).setVisibility(View.GONE);
                    }

                    @Override
                    public void requestError(VolleyError ex) {
                        findViewById(R.id.frameCloningClass).setVisibility(View.GONE);
                    }
                });
            }
        });

        // set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");

        ((TextView)findViewById(R.id.tvAddClassTitle)).setTypeface(font);
        ((TextView)findViewById(R.id.tvPreFormTitle)).setTypeface(font);
        ((TextView)findViewById(R.id.tvCopyClassTitle)).setTypeface(font);
        ((TextView)findViewById(R.id.tvAddClassCompleteTitle)).setTypeface(fontBold);
        ((TextView)findViewById(R.id.tvAddClassCompleteSubtitle)).setTypeface(font);

        findViewById(R.id.frameForm).setVisibility(View.GONE);
        findViewById(R.id.frameCopyClass).setVisibility(View.GONE);
        findViewById(R.id.framePreForm).setVisibility(View.VISIBLE);

        findViewById(R.id.hbNewClass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScreen = 1;
                findViewById(R.id.frameForm).setVisibility(View.VISIBLE);
                findViewById(R.id.framePreForm).setVisibility(View.GONE);
                setActiveStep(0);
            }
        });

        findViewById(R.id.hbCopyClass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScreen = 2;
                findViewById(R.id.frameCopyClass).setVisibility(View.VISIBLE);
                findViewById(R.id.framePreForm).setVisibility(View.GONE);
                clDraft.load();
            }
        });

        findViewById(R.id.imgIconBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });

        gallerySetup = findViewById(R.id.gallerySetup);
        gallerySetup.setErrorTextView(findViewById(R.id.tvErrorGallery));

        classSpec = (ClassSpecificationSetup)findViewById(R.id.svClassSpec);
        classSpec.setCompleteListener(new OnOperationCompleteWithIdListener() {
            @Override
            public void complete(Long id) {
                classTiming.setClassId(id);
                classDetails.setClassId(id);
                gallerySetup.setClassId(id);
                classInfoPanel.setClassId(id, true);
                setActiveStep(currentStep + 1);
            }
        });

        classTiming = (ClassTimingSetup)findViewById(R.id.svClassTiming);
        classTiming.init(findViewById(R.id.dtsAddClass));
        classTiming.setCompleteListener(new OnOperationCompleteWithIdListener() {
            @Override
            public void complete(Long id) {
                setActiveStep(currentStep + 1);
            }
        });

        classDetails = (ClassDetailsSetup)findViewById(R.id.svClassDetails);
        classDetails.setAddImageListener(new OnButtonClickListener() {
            @Override
            public void clicked(IconButton button) {
                gallerySetup.setClassId(classSpec.getClassId());
                gallerySetup.load();
                findViewById(R.id.frameGalleryManager).setVisibility(View.VISIBLE);
            }
        });
        classDetails.setCompleteListener(new OnOperationCompleteWithIdListener() {
            @Override
            public void complete(Long id) {
                classDetails.setClassId(id);
                setActiveStep(currentStep + 1);
            }
        });

        classInfoPanel = findViewById(R.id.classInfoPanel);
        classInfoPanel.setCompleteListener(new OnClassInfoActionListener() {
            @Override
            public void main(String action) {
                setActiveStep(currentStep + 1);
            }

            @Override
            public void secondary() {
                setActiveStep(currentStep - 1);
            }
        });

        setActiveStep(0);

        findViewById(R.id.frameGalleryManager).setVisibility(View.GONE);
        findViewById(R.id.rbDoneGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.frameGalleryManager).setVisibility(View.GONE);
                classDetails.setGalleryImageCount(gallerySetup.getGalleryPaths().size());
            }
        });

        findViewById(R.id.rbClassGoDashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void moveBack() {
        if(mainScreen == 0)
            finish();
        else if(mainScreen == 1 && currentStep > 0)
            setActiveStep(currentStep - 1);
        else if(mainScreen == 1 && currentStep == 0) {
            mainScreen = 0;
            findViewById(R.id.frameForm).setVisibility(View.GONE);
            findViewById(R.id.framePreForm).setVisibility(View.VISIBLE);
        }
        else if(mainScreen == 2) {
            mainScreen = 0;
            findViewById(R.id.frameCopyClass).setVisibility(View.GONE);
            findViewById(R.id.framePreForm).setVisibility(View.VISIBLE);
        }
    }



    private void setActiveStep(int step) {
        currentStep = step;
        for(int i = 0; i < wizardSteps.size(); i++) {
            wizardSteps.get(i).setActive(step == i, step > i);
        }

        classSpec.setVisibility(step == 0 ? View.VISIBLE : View.GONE);
        classTiming.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        classDetails.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        classInfoPanel.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        findViewById(R.id.svAddClassComplete).setVisibility(step == 4 ? View.VISIBLE : View.GONE);

        ((ImageView)findViewById(R.id.imgLine1)).setImageResource(R.drawable.line_inactive);
        ((ImageView)findViewById(R.id.imgLine2)).setImageResource(R.drawable.line_inactive);

        if(step > 0)
            ((ImageView)findViewById(R.id.imgLine1)).setImageResource(R.drawable.line_active);
        if(step > 1)
            ((ImageView)findViewById(R.id.imgLine2)).setImageResource(R.drawable.line_active);
    }

    @Override
    public void onBackPressed() {
        moveBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constant.PickReason.GALLERY) {
                gallerySetup.doUploadImage(data);
        }
    }
}
